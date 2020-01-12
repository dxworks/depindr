package depindr;

import depindr.json.Dependency;
import depindr.model.Author;
import depindr.model.Commit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DepinderResult {
    private String name;
    private String category;
    private String file;
    private Integer value;

    private Commit commit;
    private DepinderFile depinderFile;
    private Author author;
    private Dependency dependency;
}
