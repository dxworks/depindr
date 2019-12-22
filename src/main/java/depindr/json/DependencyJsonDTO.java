package depindr.json;

import depindr.utils.ImportUtils;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Data
public class DependencyJsonDTO {
    private String category;
    private String name;
    private List<String> languages;
    private List<String> extensions;
    private List<String> fingerprints;

    private boolean wrapAsImports;

    public static DependencyJsonDTO fromDependency(Dependency technology) {
        DependencyJsonDTO technologyJsonDTO = new DependencyJsonDTO();

        technologyJsonDTO.setName(technology.getName());
        technologyJsonDTO.setCategory(technology.getCategory());
        technologyJsonDTO.setExtensions(technology.getExtensions());
        technologyJsonDTO.setLanguages(technology.getLanguages());

        AtomicBoolean shouldWrapAsImports = new AtomicBoolean(false);

        List<String> fingerPrints = technology.getFingerprints().stream().map(fingerPrint -> {
            if (fingerPrint.contains(ImportUtils.IMPORT_SUFFIX)) {
                shouldWrapAsImports.set(true);
                return ImportUtils.unwrapImportPackage(fingerPrint);
            }
            return fingerPrint;
        }).collect(Collectors.toList());

        technologyJsonDTO.setFingerprints(fingerPrints);
        technologyJsonDTO.setWrapAsImports(shouldWrapAsImports.get());

        return technologyJsonDTO;
    }

    public Dependency toDependency() {
        Dependency technology = new Dependency();

        technology.setName(name);
        technology.setCategory(category);
        technology.setLanguages(languages != null ? languages : Collections.emptyList());
        technology.setExtensions(extensions != null ? extensions : Collections.emptyList());
        technology.setFingerprints(fingerprints == null ? Collections.emptyList() : wrapAsImports ? fingerprintsWrappedAsImports() : fingerprints);

        return technology;
    }

    private List<String> fingerprintsWrappedAsImports() {
        return fingerprints.stream().map(ImportUtils::wrapImportPackage).collect(Collectors.toList());
    }

}
