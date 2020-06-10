package depindr.analyzers;

import depindr.Depinder;
import depindr.DepinderResult;
import depindr.exceptions.DepinderException;
import depindr.json.Dependency;
import depindr.model.entity.Commit;
import depindr.model.snapshot.AppearanceSnapshot;
import depindr.model.snapshot.Snapshot;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static depindr.utils.FileUtils.writeSnapshotsToFile;

public class AppearanceOfATechAnalyzer implements DepinderCommand {

    public void execute(Depinder depinder, String[] args) {
        depinder.getDependencyRegistry().getAll().forEach(dependency -> {
            List<Commit> commits = getCommitsInChronologicalOrder(dependency);

            commits.stream().findFirst().ifPresent(commit -> {
                System.out.printf("Dependency %s appeared in commit %s on %s%n authored by %s\n",
                        dependency.getName(), commit.getID(), commit.getAuthorTimestamp().toString(), commit.getAuthor().getID().getName());

                List<Snapshot> yolo = new ArrayList<>();

                AppearanceSnapshot snapshot = AppearanceSnapshot.builder()
                        .nameOfDependency(dependency.getName())
                        .snapshotTimestamp(commit.getAuthorTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .commitID(commit.getID())
                        .authorName(commit.getAuthor().getID().getName())
                        .build();

                yolo.add(snapshot);

                Path filePath = Paths.get("results", "appearance", commit.getID() + ".json");
                try {
                    writeSnapshotsToFile(yolo, filePath);
                } catch (IOException e) {
                    throw new DepinderException("Could not write Spread Result snapshot to file.", e);
                }
            });
        });
    }

    @NotNull
    private List<Commit> getCommitsInChronologicalOrder(Dependency dependency) {
        return dependency.getDepinderResults().stream()
                .map(DepinderResult::getCommit)
                .sorted(Comparator.comparing(Commit::getAuthorTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public boolean parse(String[] args) {
        return args.length == 3;
    }

    @Override
    public String usage() {
        return "depinder --appearance <json_output_name_for_results> <flag_for_removal_of_comments>";
    }

}
