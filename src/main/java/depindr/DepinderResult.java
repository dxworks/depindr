package depindr;

import depindr.json.Dependency;
import depindr.model.dto.FingerprintMatch;
import depindr.model.entity.Author;
import depindr.model.entity.Commit;
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
