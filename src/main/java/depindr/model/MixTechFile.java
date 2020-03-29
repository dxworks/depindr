package depindr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MixTechFile {
    private String fileName;
    private int numberOfTechs;
    private List<TechUsage> techUsages;
}
