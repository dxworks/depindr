package depindr;

import depindr.json.Dependency;
import depindr.model.Author;
import depindr.model.Commit;
import depindr.model.FingerprintMatch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DepinderResult {
    private String name;
    private String category;
    private String file;
    private Integer value;

    private List<FingerprintMatch> fingerprintMatches;

    private Commit commit;
    private DepinderFile depinderFile;
    private Author author;
    private Dependency dependency;
}
