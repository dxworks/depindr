package depindr;


import depindr.configuration.DepinderConfiguration;
import depindr.constants.DepinderConstants;
import depindr.exceptions.DepinderException;
import depindr.git.GitClient;
import depindr.json.Dependency;
import depindr.json.JsonFingerprintParser;
import depindr.model.Author;
import depindr.model.AuthorRegistry;
import depindr.model.Commit;
import depindr.model.CommitRegistry;
import depindr.model.dto.CommitDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/*
 * #DONE 0.Read configuration file
 * #DONE 1.Read dependecies from .json file and print on console the file2hj[;l,ikp v
 * #TODO 2. Read repository using JGit (checkout commits, read al files, create commit object, match Dependencies on all files)
 * */

@Slf4j
public class Depinder {
    public static void main(String[] args) {
        System.out.print("Reading configuration file: \n");

        Path configurationFilePath = Paths.get(DepinderConstants.CONFIGURATION_FOLDER, DepinderConstants.CONFIGURATION_FILE);

        File resultsFolder = new File(DepinderConstants.RESULTS_FOLDER);
        if (!resultsFolder.exists())
            resultsFolder.mkdirs();

        Properties properties = new Properties();
        try {
            properties.load(new FileReader(configurationFilePath.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DepinderConfiguration.loadProperties(properties);

        List<Dependency> dependencies = readDependencyFingerprints();

        String rootFolder = DepinderConfiguration.getInstance().getProperty(DepinderConstants.ROOT_FOLDER);

        AuthorRegistry authorRegistry = new AuthorRegistry();
        CommitRegistry commitRegistry = new CommitRegistry();

        //check if root folder is a git repository
        GitClient gitClient = new GitClient();
        List<CommitDTO> allCommitNames = gitClient.getAllCommitNames(rootFolder);

        //checkout each commit, read files, match fingerprints, create models.
        for (CommitDTO commitDTO : allCommitNames) {
            Author author = Author.fromDTO(commitDTO.getAuthor());
            author = authorRegistry.add(author);

            Commit commit = Commit.fromDTO(commitDTO);
            commit.setAuthor(author);
            commit = commitRegistry.add(commit);

            gitClient.checkoutCommitForRepo(rootFolder, commitDTO.getCommitID());
            List<DepinderFile> depinderFiles = readFilesFromRepo(rootFolder);
            for (Dependency dependency : dependencies) {
                for (DepinderFile depinderFile : depinderFiles) {
                    List<DepinderResult> depinderResults = dependency.analyze(depinderFile);
                    for (DepinderResult depinderResult : depinderResults) {
                        depinderResult.setCommit(commit);
                        commit.addResult(depinderResult);
                        depinderResult.setAuthor(author);
                        author.addResult(depinderResult);
                    }
                }
            }
        }

        System.out.println("gata");

//        gitClient.cloneRepo("https://github.com/bilbor987/AT_parser.git");
//        gitClient.cloneRepo("https://github.com/bilbor987/DepindR_2.0.git");
//        gitClient.printAllCommits("AT_parser");
//        gitClient.printAllCommits("DepindR_2.0");


    }

    private static List<DepinderFile> readFilesFromRepo(String rootFolder) {
        try {
            return Files.walk(Paths.get(rootFolder))
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.toFile().getAbsolutePath().contains(".git"))
                    .map(path -> {
                        try {
                            return DepinderFile.builder()
                                    .path(path.toAbsolutePath().toString())
                                    .name(path.getFileName().toString())
                                    .content(new String(Files.readAllBytes(path)))
                                    .build();
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

    public static List<Dependency> readDependencyFingerprints() {
        String rootFolder = DepinderConfiguration.getInstance().getProperty(DepinderConstants.JSON_FINGERPRINT_FILES);

        JsonFingerprintParser jsonFingerprintParser = new JsonFingerprintParser();
        return jsonFingerprintParser.parseTechnologiesFile(rootFolder);
    }

}
