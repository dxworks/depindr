package depindr.analyzers;

import depindr.Depinder;
import depindr.configuration.DepinderConfiguration;
import depindr.exceptions.DepinderException;
import depindr.model.entity.Commit;
import depindr.model.snapshot.AppearanceSnapshot;
import depindr.model.snapshot.Snapshot;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static depindr.utils.FileUtils.*;

public class AppearanceOfATechAnalyzer implements DepinderCommand {

    public void execute(Depinder depinder, String[] args) {
        String folderName = "Appearance_per_commit";

        depinder.getDependencyRegistry().getAll().forEach(dependency -> {
            List<Commit> commits = getCommitsInChronologicalOrder(dependency);

            commits.stream().findFirst().ifPresent(commit -> {
//                System.out.printf("Dependency %s appeared in commit %s on %s%n authored by %s\n",
//                        dependency.getName(), commit.getID(), commit.getAuthorTimestamp().toString(), commit.getAuthor().getID().getName());
                List<Snapshot> appearanceSnapshots = new ArrayList<>();

                AppearanceSnapshot snapshot = AppearanceSnapshot.builder()
                        .nameOfDependency(dependency.getName())
                        .snapshotTimestamp(commit.getAuthorTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .commitID(commit.getID())
                        .authorName(commit.getAuthor().getID().getName())
                        .build();

                appearanceSnapshots.add(snapshot);

                CreateOutputFolder(folderName);

                Path filePath = Paths.get("results", DepinderConfiguration.getInstance().getProjectID(), folderName, commit.getID() + ".json");
                try {
                    writeSnapshotsToFile(appearanceSnapshots, filePath);
                } catch (IOException e) {
                    throw new DepinderException("Could not write Appearance Result snapshot to file.", e);
                }
            });
        });
    }


    @Override
    public boolean parse(String[] args) {
        return args.length == 2;
    }

    @Override
    public String usage() {
        return "depinder --appearance <flag_for_removal_of_comments>";
    }

}
