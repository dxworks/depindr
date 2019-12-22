package depindr;

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
}
