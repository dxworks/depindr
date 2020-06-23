package depindr.analyzers;

import depindr.Depinder;
import depindr.DepinderFile;
import depindr.DepinderResult;
import depindr.configuration.DepinderConfiguration;
import depindr.exceptions.DepinderException;
import depindr.model.dto.AuthorID;
import depindr.model.entity.Author;
import depindr.model.entity.Commit;
import depindr.model.snapshot.Snapshot;
import depindr.model.snapshot.TechnologySnapshot;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static depindr.utils.FileUtils.CreateOutputFolder;
import static depindr.utils.FileUtils.writeSnapshotsToFile;

public class SpreadOfATechAnalyzer implements DepinderCommand {
    //at all commits

    public void execute(Depinder depinder, String[] args) {
        String folderName = "Spread_per_commit";
        String fileName = args[1];
        String tech = args[2];

        List<Snapshot> snapshots = depinder.getCommitRegistry().getAll().stream()
                .sorted(Comparator.comparing(Commit::getAuthorTimestamp))
                .flatMap(commit -> {
                    Map<String, List<DepinderResult>> techToResults = commit.getResults().stream()
                            .collect(Collectors.groupingBy(depinderResult -> depinderResult.getDependency().getID()));

                    List<Snapshot> commitSnapshots = techToResults.keySet().stream()
                            .map(techId -> {
                                List<String> files = techToResults.get(techId).stream()
                                        .map(DepinderResult::getDepinderFile)
                                        .map(DepinderFile::getName)
                                        .collect(Collectors.toList());

                                List<String> authors = techToResults.get(techId).stream()
                                        .map(DepinderResult::getAuthor)
                                        .map(Author::getID)
                                        .map(AuthorID::getName)
                                        .distinct()
                                        .collect(Collectors.toList());

                                int value = techToResults.get(techId).stream()
                                        .mapToInt(DepinderResult::getValue).sum();

                                return TechnologySnapshot.builder()
                                        .commitID(commit.getID())
                                        .value(value)
                                        .authors(authors)
                                        .nrAuthors(authors.size())
//                                        .snapshotTimestamp(commit.getAuthorTimestamp().toInstant().toEpochMilli())
                                        .snapshotTimestamp(commit.getAuthorTimestamp().format(DateTimeFormatter.ISO_INSTANT))
                                        .techId(techId)
                                        .files(files)
                                        .numberOfFiles(files.size())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    CreateOutputFolder(folderName);

                    Path filePath = Paths.get("results", DepinderConfiguration.getInstance().getProjectID(), folderName, commit.getID() + ".json");
                    try {
                        writeSnapshotsToFile(commitSnapshots, filePath);
                    } catch (IOException e) {
                        throw new DepinderException("Could not write Spread Result snapshot to file.", e);
                    }

                    return commitSnapshots.stream();
                })
                .collect(Collectors.toList());

        Path filePath = Paths.get("results", DepinderConfiguration.getInstance().getProjectID(), fileName);
        try {
            writeSnapshotsToFile(snapshots, filePath);
        } catch (IOException e) {
            throw new DepinderException("Could not write Spread Result snapshot to file.", e);
        }

    }

    @Override
    public boolean parse(String[] args) {
        return args.length == 3;
    }

    @Override
    public String usage() {
        return "depinder --spread <json_output_name_for_results> <flag_for_removal_of_comments>";
    }
}
