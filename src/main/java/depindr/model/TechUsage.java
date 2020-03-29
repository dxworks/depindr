package depindr.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TechUsage {
    private String techName;
    private int usage;
}
