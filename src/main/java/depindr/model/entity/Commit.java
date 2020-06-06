package depindr.model.entity;

import depindr.DepinderResult;
import depindr.model.dto.CommitDTO;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Commit implements Entity<String> {

    private String id;
    private Author author;
    private ZonedDateTime authorTimestamp;
    private final List<DepinderResult> results = new ArrayList<>();

    private FileRegistry fileRegistry;

    public static Commit fromDTO(CommitDTO commitDTO) {
        Commit commit = new Commit();
        commit.id = commitDTO.getCommitID();
        commit.authorTimestamp = commitDTO.getAuthorTimestamp();

        return commit;
    }

    @Override
    public String getID() {
        return id;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void addResult(DepinderResult depinderResult) {
        results.add(depinderResult);
    }

    public void setFileRegistry(FileRegistry fileRegistry) {
        this.fileRegistry = fileRegistry;
    }

    public ZonedDateTime getAuthorTimestamp() {
        return authorTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commit commit = (Commit) o;
        return Objects.equals(id, commit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public List<DepinderResult> getResults() {
        return results;
    }

    public FileRegistry getFileRegistry() {
        return fileRegistry;
    }

    public Author getAuthor() {
        return author;
    }
}
