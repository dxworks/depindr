package depindr.json;

import lombok.Data;

import java.util.List;

@Data
public class JsonConfigurationDTO {
    private String outputFileName;

    private boolean useProjectPrefix = true;

    private List<DependencyJsonDTO> technologies;
}
