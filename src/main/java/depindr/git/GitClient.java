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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class GitClient {

    public List<CommitDTO> getAllCommits(String repoPath) throws NoSuchGitRepoException {
        System.out.println("Commits from " + repoPath);
        try {
            final Git git = getRepository(repoPath);

            return StreamSupport.stream(
                    git.log()
                            .call()
                            .spliterator(), true)
                    .map(commit -> createCommitDTO(git.getRepository(), commit))
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

    private Git getRepository(String rootFolder) {
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

    private CommitDTO createCommitDTO(Repository repository, RevCommit commit) {
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
                .modifiedFiles(getCommitChangedFiles(repository, commit))
                .message(CommitMessageDTO.builder()
                        .fullDescription(commit.getFullMessage())
                        .shortDescription(commit.getShortMessage())
                        .build())
                .build();
    }


    private List<String> getCommitChangedFiles(Repository repository, RevCommit revCommit) {
        ObjectReader reader = repository.newObjectReader();
        AbstractTreeIterator parentTreeIterator = new CanonicalTreeParser();
        CanonicalTreeParser currentCommitTreeIterator = new CanonicalTreeParser();
        List<DiffEntry> diffs = null;

        try (
                DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
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

        return CollectionUtils.emptyIfNull(diffs).stream()
                .map(diff -> diff.getNewPath().equals("/dev/null") ? diff.getOldPath() : diff.getNewPath())
                .collect(Collectors.toList());
    }


    public static class NoSuchGitRepoException extends RuntimeException {
        public NoSuchGitRepoException(String rootFolder, Throwable cause) {
            super(rootFolder + " is not a git repository", cause);
        }
    }
}
