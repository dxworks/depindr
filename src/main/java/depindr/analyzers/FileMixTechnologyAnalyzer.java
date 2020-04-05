package depindr.analyzers;

import depindr.DepinderFile;
import depindr.DepinderResult;
import depindr.model.CommitRegistry;
import depindr.model.MixTechFile;
import depindr.model.MixTechnologySnapshot;
import depindr.model.TechUsage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileMixTechnologyAnalyzer {

    private TechUsage transformDepindrResultToTechUsage(DepinderResult depinderResult) {
        return new TechUsage(depinderResult.getDependency().getID(), depinderResult.getValue());
    }

    private MixTechFile transformDepindrFileToMixTechFile(DepinderFile depinderFile, int threshold) {
        long count = depinderFile.getResults().size();

        if (count >= threshold) {
            return MixTechFile.builder()
                    .fileName(depinderFile.getFullyQualifiedName())
                    .numberOfTechs((int) count)
                    .techUsages(depinderFile.getResults().parallelStream()
                            .map(this::transformDepindrResultToTechUsage).collect(Collectors.toList()))
                    .build();
        }

        return null;
    }

    public List<MixTechnologySnapshot> analzye(CommitRegistry commitRegistry, int threshold) {
        return commitRegistry.getAll().parallelStream()
                .map(commit -> {
                    List<MixTechFile> mixTechFiles = commit.getFileRegistry().getAll().parallelStream()
                            .map(depinderFile -> transformDepindrFileToMixTechFile(depinderFile, threshold))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    return MixTechnologySnapshot.builder()
                            .commitID(commit.getID())
                            .snapshotTimestamp(commit.getAuthorTimestamp())
                            .files(mixTechFiles)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
