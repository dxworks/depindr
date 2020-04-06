package depindr;

import depindr.exceptions.DepinderException;
import depindr.git.GitClient;
import depindr.json.Dependency;
import depindr.json.JsonFingerprintParser;
import depindr.model.*;
import depindr.model.dto.CommitDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static depindr.utils.FileUtils.removeComments;

@Slf4j
@Data
public class Depinder {

    private AuthorRegistry authorRegistry;
    private CommitRegistry commitRegistry;
    private DependencyRegistry dependencyRegistry;

    public Depinder(String dependencyJsonFile) {
        authorRegistry = new AuthorRegistry();
        commitRegistry = new CommitRegistry();
        dependencyRegistry = new DependencyRegistry();

        dependencyRegistry.addAll(readDependencyFingerprints(dependencyJsonFile));
    }

    public void analyzeProject(String rootFolder, String branchName, boolean removeCommentsFlag) {
        //check if root folder is a git repository
        GitClient gitClient = new GitClient(rootFolder);

        gitClient.checkoutCommitForRepo(branchName);

        List<CommitDTO> allCommitNames = gitClient.getAllCommits(rootFolder).stream().sorted(Comparator.comparing(CommitDTO::getAuthorTimestamp)).collect(Collectors.toList());

        //checkout each commit, read files, match fingerprints, create models.
        for (CommitDTO commitDTO : allCommitNames) {
            Author author = Author.fromDTO(commitDTO.getAuthor());
            author = authorRegistry.add(author);

            FileRegistry fileRegistry = new FileRegistry();

            Commit commit = Commit.fromDTO(commitDTO);
            commit.setAuthor(author);
            commit = commitRegistry.add(commit);
            commit.setFileRegistry(fileRegistry);

            gitClient.checkoutCommitForRepo(commitDTO.getCommitID());

            List<Path> modifiedFilePaths = commitDTO.getModifiedFiles().parallelStream().map(s -> Paths.get(rootFolder, s)).collect(Collectors.toList());
            List<DepinderFile> depinderFiles = readFilesFromRepo(rootFolder, fileRegistry, removeCommentsFlag, modifiedFilePaths);
            Set<String> deletedFiles = modifiedFilePaths.stream().map(path -> path.toAbsolutePath().toString()).collect(Collectors.toSet());

            deletedFiles.removeAll(depinderFiles.stream().map(DepinderFile::getPath).collect(Collectors.toSet()));

            for (Dependency dependency : dependencyRegistry.getAll()) {
                for (DepinderFile depinderFile : depinderFiles) {
                    DepinderResult depinderResult = dependency.analyze(depinderFile);
                    if (depinderResult != null) {
                        depinderResult.setCommit(commit);
                        commit.addResult(depinderResult);
                        depinderResult.setAuthor(author);
                        author.addResult(depinderResult);
                    }
                }
            }

            fileRegistry.getAll().forEach(depinderFile -> depinderFile.setContent(null));

        }

        for (CommitDTO commitDTO : allCommitNames) {
            List<String> deletedFiles = commitDTO.getDeletedFiles();
            Optional<Commit> parentCommitOptional = commitRegistry.getById(commitDTO.getFirstParentID());
            Commit currentCommit = commitRegistry.getById(commitDTO.getCommitID()).get();
            if (parentCommitOptional.isPresent()) {
                List<DepinderResult> parentCommitResults = parentCommitOptional.get().getResults();
                List<DepinderResult> newResults = parentCommitResults.stream()
                        .filter(depinderResult -> !deletedFiles.contains(depinderResult.getDepinderFile().getPath()))
                        .filter(depinderResult -> currentCommit.getResults().stream()
                                .noneMatch(dr -> depinderResult.getFile().equals(dr.getFile()) && depinderResult.getName().equals(dr.getName())
                                        && depinderResult.getCategory().equals(dr.getCategory()))).collect(Collectors.toList());

                newResults.stream()
                        .map(res -> DepinderResult.builder()
                                .name(res.getName())
                                .file(res.getFile())
                                .category(res.getCategory())
                                .value(res.getValue())
                                .fingerprintMatches(res.getFingerprintMatches())
                                .author(authorRegistry.getById(new AuthorID(commitDTO.getAuthor().getName(), commitDTO.getAuthor().getEmail())).get())
                                .commit(currentCommit)
                                .dependency(res.getDependency())
                                .depinderFile(getDepinderFileAndAddItToCurrentCommit(res, currentCommit))
                                .build())
                        .forEach(depinderResult -> {
                            depinderResult.getAuthor().addResult(depinderResult);
//                            depinderResult.getDepinderFile().addResult(depinderResult);
                            depinderResult.getDependency().addResult(depinderResult);
                            currentCommit.addResult(depinderResult);
                        });
            }

        }
    }

    private DepinderFile getDepinderFileAndAddItToCurrentCommit(DepinderResult res, Commit currentCommit) {
        currentCommit.getFileRegistry().add(res.getDepinderFile());
        return res.getDepinderFile();
    }

    private List<DepinderFile> readFilesFromRepo(String rootFolder, FileRegistry fileRegistry, boolean flag, List<Path> modifiedFilePaths) {
        try {
            return Files.walk(Paths.get(rootFolder))
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.toFile().getAbsolutePath().contains(".git"))
                    .filter(modifiedFilePaths::contains)
                    .map(path -> {
                        try {
                            DepinderFile depinderFile = DepinderFile.builder()
                                    .path(path.toAbsolutePath().toString())
                                    .name(path.getFileName().toString())
                                    .extension(FilenameUtils.getExtension(path.getFileName().toString()))
                                    .content(readFileContentWithLfEnding(path, flag))
                                    .build();
                            fileRegistry.add(depinderFile);
                            return depinderFile;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new DepinderException("Could not read from folder " + rootFolder, e);
        }
    }

    private String readFileContentWithLfEnding(Path path, boolean flag) throws IOException {
        String s = new String(Files.readAllBytes(path))
                .replaceAll("\\r\\n", "\n") //remove Windows line endings
                .replaceAll("\\r", "\n"); //remove Mac line endings
        if (flag)
            s = removeComments(s);
        return s;
    }

    private List<Dependency> readDependencyFingerprints(String dependencyJsonFile) {

        JsonFingerprintParser jsonFingerprintParser = new JsonFingerprintParser();
        return jsonFingerprintParser.parseTechnologiesFile(dependencyJsonFile);
    }

    public DependencyRegistry getDependencyRegistry() {
        return dependencyRegistry;
    }
}
