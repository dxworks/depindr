package depindr.git;

import depindr.model.dto.AuthorDTO;
import depindr.model.dto.CommitDTO;
import depindr.model.dto.CommitMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class GitClient {

    public List<CommitDTO> getAllCommitNames(String repoPath) throws NoSuchGitRepoException {
        System.out.println("Commits from " + repoPath);
        try {
            final Git git = getRepository(repoPath);

            return StreamSupport.stream(
                    git.log()
                            .call()
                            .spliterator(), true)
                    .map(this::createCommitDTO)
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

    Git getRepository(String rootFolder) {
        try {
            return Git.open(Paths.get(rootFolder, ".git").toFile());
        } catch (IOException e) {
            log.error("could not find repository " + rootFolder, e);
            throw new NoSuchGitRepoException(rootFolder, e);
        }
    }

    public void checkoutCommitForRepo(String rootFolder, String commitName) {
        Git git = getRepository(rootFolder);

        try {
            git.checkout().setName(commitName).call();
        } catch (GitAPIException e) {
            log.error("Could not checkout commit " + commitName, e);
        }
    }

    private CommitDTO createCommitDTO(RevCommit commit) {
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
                .message(CommitMessageDTO.builder()
                        .fullDescription(commit.getFullMessage())
                        .shortDescription(commit.getShortMessage())
                        .build())
                .build();
    }


    public static class NoSuchGitRepoException extends RuntimeException {
        public NoSuchGitRepoException(String rootFolder, Throwable cause) {
            super(rootFolder + " is not a git repository", cause);
        }
    }
}
