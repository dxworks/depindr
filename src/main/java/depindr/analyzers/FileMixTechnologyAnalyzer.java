package depindr.analyzers;

import depindr.Depinder;
import depindr.DepinderFile;
import depindr.DepinderResult;
import depindr.configuration.DepinderConfiguration;
import depindr.exceptions.DepinderException;
import depindr.model.snapshot.MixTechFile;
import depindr.model.snapshot.MixTechnologySnapshot;
import depindr.model.snapshot.Snapshot;
import depindr.model.snapshot.TechUsage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static depindr.utils.FileUtils.writeSnapshotsToFile;

public class FileMixTechnologyAnalyzer implements DepinderCommand {

    public void execute(Depinder depinder, String[] args) {

        int threshold;
        String fileName;
        if (args[0].equals("--all")) {
            fileName = args[3];
            threshold = Integer.parseInt(args[4]);
        } else {
            threshold = Integer.parseInt(args[1]);
            fileName = args[2];
        }

        int finalThreshold = threshold;
        List<Snapshot> mixTechnologySnapshots = depinder.getCommitRegistry().getAll().stream()
                .map(commit -> {
                    List<MixTechFile> mixTechFiles = commit.getFileRegistry().getAll().stream()
                            .map(depinderFile -> transformDepinderFileToMixTechFile(depinderFile, finalThreshold))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    return MixTechnologySnapshot.builder()
                            .commitID(commit.getID())

                            .snapshotTimestamp(commit.getAuthorTimestamp().format(DateTimeFormatter.ISO_INSTANT))
                            .files(mixTechFiles)
                            .build();
                })
                .collect(Collectors.toList());

        File resultsFolder = new File("results" + File.separator + DepinderConfiguration.getInstance().getProjectID());
        if (!resultsFolder.exists())
            //noinspection ResultOfMethodCallIgnored
            resultsFolder.mkdirs();

        Path filePath = Paths.get("results", DepinderConfiguration.getInstance().getProjectID(), fileName);
        try {
            writeSnapshotsToFile(mixTechnologySnapshots, filePath);
        } catch (IOException e) {
            throw new DepinderException("Could not write File Mix Result snapshot to file.", e);
        }
    }

    private TechUsage transformDepinderResultToTechUsage(DepinderResult depinderResult) {
        return new TechUsage(depinderResult.getDependency().getID(), depinderResult.getValue());
    }

    private MixTechFile transformDepinderFileToMixTechFile(DepinderFile depinderFile, int threshold) {
        long count = depinderFile.getResults().size();

        if (count >= threshold) {
            return MixTechFile.builder()
                    .fileName(depinderFile.getFullyQualifiedName())
                    .numberOfTechs((int) count)
                    .techUsages(depinderFile.getResults().stream()
                            .map(this::transformDepinderResultToTechUsage).collect(Collectors.toList()))
                    .build();
        }

        return null;
    }

    @Override
    public boolean parse(String[] args) {
        Pattern pattern = Pattern.compile("\\.json$");
        Matcher matcher = pattern.matcher(args[1]);

        if (args.length != 4)
            return false;

        if (!matcher.find()) {
            System.out.println("File format not supported. Please provide a <mix.json> file name!");
            return false;
        }

        if ((!args[3].matches("true")) && (!args[3].matches("false"))) {
            System.out.println("Flag not supported. Please provide true/false");
            return false;
        }

        return true;
    }

    @Override
    public String usage() {
        return "depinder --mix <threshold(integer value)> <json_output_name_for_results> <flag_for_removal_of_comments>";
    }
}
