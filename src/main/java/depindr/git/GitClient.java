package depindr.git;

import depindr.model.dto.AuthorDTO;
import depindr.model.dto.CommitDTO;
import depindr.model.dto.CommitMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class GitClient {

    private final Git gitObject;
    private int commitNumber = 0;

    public GitClient(String rootFolder) {
        try {
            gitObject = Git.open(Paths.get(rootFolder, ".git").toFile());
        } catch (IOException e) {
            log.error("could not find repository " + rootFolder, e);
            throw new NoSuchGitRepoException(rootFolder, e);
        }
    }

    public List<CommitDTO> getAllCommits(String repoPath) throws NoSuchGitRepoException {
        System.out.println("Commits from " + repoPath);
        try {
            return StreamSupport.stream(
                    gitObject.log()
                            .call()
                            .spliterator(), true)
                    .map(commit -> createCommitDTO(gitObject.getRepository(), commit))
                    .collect(Collectors.toList());

        } catch (GitAPIException e) {
            log.error("Git Api error", e);
            throw new NoSuchGitRepoException(repoPath, e);
        }
    }

    private ZonedDateTime createZonedTimeDate(PersonIdent personIdent) {
        Date date = personIdent.getWhen();
        TimeZone timeZone = personIdent.getTimeZone();

        return date.toInstant().atZone(timeZone.toZoneId());
    }


    public void checkoutCommitForRepo(String commitName) {
        try {
            System.out.format("Commit no: %d\n", commitNumber++);
            gitObject.checkout().setName(commitName).call();
        } catch (GitAPIException e) {
            log.error("Could not checkout commit " + commitName, e);
        }
    }

    private CommitDTO createCommitDTO(Repository repository, RevCommit commit) {
        List<String> modifiedFiles = new ArrayList<>();
        List<String> deletedFiles = new ArrayList<>();
        getCommitChangedFiles(repository, commit, modifiedFiles, deletedFiles);
        return CommitDTO.builder()
                .author(AuthorDTO.builder()
                        .name(commit.getAuthorIdent().getName())
                        .email(commit.getAuthorIdent().getEmailAddress())
                        .build())
                .committer(AuthorDTO.builder()
                        .name(commit.getCommitterIdent().getName())
                        .email(commit.getCommitterIdent().getEmailAddress())
                        .build())
                .authorTimestamp(createZonedTimeDate(commit.getAuthorIdent()))
                .committerTimestamp(createZonedTimeDate(commit.getCommitterIdent()))
                .commitID(commit.getName())
                .parentIDs(Arrays.stream(commit.getParents())
                        .map(AnyObjectId::name)
                        .toArray(String[]::new))
                .modifiedFiles(modifiedFiles)
                .deletedFiles(deletedFiles)
                .message(CommitMessageDTO.builder()
                        .fullDescription(commit.getFullMessage())
                        .shortDescription(commit.getShortMessage())
                        .build())
                .build();
    }

    private void getCommitChangedFiles(Repository repository, RevCommit revCommit, List<String> modifiedFiles, List<String> deletedFiles) {
        ObjectReader reader = repository.newObjectReader();
        AbstractTreeIterator parentTreeIterator = new CanonicalTreeParser();
        CanonicalTreeParser currentCommitTreeIterator = new CanonicalTreeParser();
        List<DiffEntry> diffs = null;

        try (
                DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE)
        ) {
            if (revCommit.getParentCount() == 0) {
                parentTreeIterator = new EmptyTreeIterator();
            } else {
                RevCommit parentCommit = revCommit.getParent(0);
                ((CanonicalTreeParser) parentTreeIterator).reset(reader, parentCommit.getTree().getId());
            }
            currentCommitTreeIterator.reset(reader, revCommit.getTree().getId());

            df.setRepository(repository);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);

            diffs = df.scan(parentTreeIterator, currentCommitTreeIterator);
        } catch (IOException e) {
            e.printStackTrace();
        }

        modifiedFiles.addAll(CollectionUtils.emptyIfNull(diffs).stream()
                .map(diff -> diff.getNewPath().equals("/dev/null") ? diff.getOldPath() : diff.getNewPath())
                .collect(Collectors.toList()));

        deletedFiles.addAll(CollectionUtils.emptyIfNull(diffs).stream()
                .filter(diff -> !diff.getNewPath().equals(diff.getOldPath()))
                .map(DiffEntry::getOldPath)
                .filter(path -> !path.equals("/dev/null"))
                .collect(Collectors.toList()));
    }

    public static class NoSuchGitRepoException extends RuntimeException {
        NoSuchGitRepoException(String rootFolder, Throwable cause) {
            super(rootFolder + " is not a git repository", cause);
        }
    }
}
