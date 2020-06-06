package depindr.json;

import depindr.utils.ImportUtils;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class DependencyJsonDTO {
    private String category;
    private String name;
    private List<String> languages;
    private List<String> extensions;
    private List<String> fingerprints;

    private boolean wrapAsImports;

    public Dependency toDependency() {
        return new Dependency(
                category,
                name,
                languages != null ? languages : Collections.emptyList(),
                extensions != null ? extensions : Collections.emptyList(),
                fingerprints == null ? Collections.emptyList() : wrapAsImports ? fingerprintsWrappedAsImports() : fingerprints);
    }

    private List<String> fingerprintsWrappedAsImports() {
        return fingerprints.stream().map(ImportUtils::wrapImportPackage).collect(Collectors.toList());
    }
}
