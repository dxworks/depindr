package depindr.model;

import depindr.DepinderResult;
import depindr.model.dto.CommitDTO;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Commit implements Entity<String> {

    private String id;
    private Author author;
    private ZonedDateTime authorTimestamp;
    private List<DepinderResult> results = new ArrayList<>();

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
}
