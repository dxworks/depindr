package depindr.model.entity;

import depindr.DepinderResult;
import depindr.model.dto.AuthorDTO;
import depindr.model.dto.AuthorID;

import java.util.ArrayList;
import java.util.List;

public class Author implements Entity<AuthorID> {

    private final AuthorID authorID;

    private final List<DepinderResult> results = new ArrayList<>();

    public Author(AuthorID authorID) {
        this.authorID = authorID;
    }

    public static Author fromDTO(AuthorDTO authorDTO) {
        return new Author(new AuthorID(authorDTO.getName(), authorDTO.getEmail()));
    }

    @Override
    public AuthorID getID() {
        return authorID;
    }

    public List<DepinderResult> getResults() {
        return results;
    }

    public void addResult(DepinderResult depinderResult) {
        results.add(depinderResult);
    }
}
