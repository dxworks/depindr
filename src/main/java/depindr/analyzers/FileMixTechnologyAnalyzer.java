package depindr.analyzers;

import depindr.Depinder;
import depindr.DepinderFile;
import depindr.DepinderResult;
import depindr.exceptions.DepinderException;
import depindr.model.snapshot.MixTechFile;
import depindr.model.snapshot.MixTechnologySnapshot;
import depindr.model.snapshot.TechUsage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static depindr.utils.FileUtils.writeSnapshotsToFile;

public class FileMixTechnologyAnalyzer implements DepinderCommand {

    public void execute(Depinder depinder, String[] args) {

        int threshold = Integer.parseInt(args[1]);

        List<MixTechnologySnapshot> mixTechnologySnapshots = depinder.getCommitRegistry().getAll().stream()
                .map(commit -> {
                    List<MixTechFile> mixTechFiles = commit.getFileRegistry().getAll().stream()
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

        Path filePath = Paths.get("results\\File_Mix_Results.json");
        try {
            writeSnapshotsToFile(mixTechnologySnapshots, filePath);
        } catch (IOException e) {
            throw new DepinderException("Could not write snapshots to file.", e);
        }
    }

    private TechUsage transformDepindrResultToTechUsage(DepinderResult depinderResult) {
        return new TechUsage(depinderResult.getDependency().getID(), depinderResult.getValue());
    }

    private MixTechFile transformDepindrFileToMixTechFile(DepinderFile depinderFile, int threshold) {
        long count = depinderFile.getResults().size();

        if (count >= threshold) {
            return MixTechFile.builder()
                    .fileName(depinderFile.getFullyQualifiedName())
                    .numberOfTechs((int) count)
                    .techUsages(depinderFile.getResults().stream()
                            .map(this::transformDepindrResultToTechUsage).collect(Collectors.toList()))
                    .build();
        }

        return null;
    }

    @Override
    public boolean parse(String[] args) {
        return args.length == 4;
    }

    @Override
    public String usage() {
        return "depinder --mix <threshold(integer value)> <json_output_name_for_results> <flag_for_removal_of_comments>";
    }
}
