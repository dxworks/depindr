package depindr.analyzers;

import depindr.Depinder;
import depindr.DepinderFile;
import depindr.DepinderResult;
import depindr.exceptions.DepinderException;
import depindr.model.snapshot.Snapshot;
import depindr.model.snapshot.TechnologySnapshot;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static depindr.utils.FileUtils.writeSnapshotsToFile;

public class SpreadOfATechAnalyzer implements DepinderCommand {
    //at all commits

    public void execute(Depinder depinder, String[] args) {
        String tech = args[2];
        AtomicInteger commitIndex = new AtomicInteger(1);
        List<Snapshot> snapshots = depinder.getCommitRegistry().getAll().stream()
                .flatMap(commit -> {
                    Map<String, List<DepinderResult>> techToResults = commit.getResults().stream()
                            .collect(Collectors.groupingBy(depinderResult -> depinderResult.getDependency().getID()));

                    List<Snapshot> commitSnapshots = techToResults.keySet().stream()
                            .map(techId -> {
                                List<String> files = techToResults.get(techId).stream()
                                        .map(DepinderResult::getDepinderFile)
                                        .map(DepinderFile::getFullyQualifiedName)
                                        .collect(Collectors.toList());

                                int value = techToResults.get(techId).stream()
                                        .mapToInt(DepinderResult::getValue).sum();

                                return TechnologySnapshot.builder()
                                        .commitID(commit.getID())
                                        .value(value)
                                        .snapshotTimestamp(commit.getAuthorTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                        .techId(techId)
                                        .files(files)
                                        .numberOfFiles(files.size())
                                        .build();
                            })
                            .collect(Collectors.toList());
                    Path filePath = Paths.get("results", commit.getID() + ".json");
                    try {
                        writeSnapshotsToFile(commitSnapshots, filePath);
                    } catch (IOException e) {
                        throw new DepinderException("Could not write Spread Result snapshot to file.", e);
                    }

                    return commitSnapshots.stream();
                })
                .collect(Collectors.toList());

        Path filePath = Paths.get("results", "Spread_Results.json");
        try {
            writeSnapshotsToFile(snapshots, filePath);
        } catch (IOException e) {
            throw new DepinderException("Could not write Spread Result snapshot to file.", e);
        }

    }

    @Override
    public boolean parse(String[] args) {
        return args.length == 4;
    }

    @Override
    public String usage() {
        return "depinder --spread <json_output_name_for_results> <technology_name> <flag_for_removal_of_comments>";
    }
}
