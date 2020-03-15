package depindr.model;

import java.util.Comparator;
import java.util.Optional;

public class CommitRegistry extends AbstractRegistry<Commit, String> {


    public Optional<Commit> getLastCommit() {
        return getAll().stream()
                .sorted(Comparator.comparing(Commit::getAuthorTimestamp).reversed())
                .findFirst();


    }
}
