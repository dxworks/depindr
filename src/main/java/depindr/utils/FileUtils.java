package depindr.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import depindr.DepinderResult;
import depindr.configuration.DepinderConfiguration;
import depindr.json.Dependency;
import depindr.model.entity.Commit;
import depindr.model.snapshot.Snapshot;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class FileUtils {

    public static String removeComments(String fileContent) {
        String ret = fileContent;

        Pattern p = Pattern.compile("(/\\*([\\S\\s]+?)\\*/)");
        Matcher m = p.matcher(ret);

        char[] charArray = ret.toCharArray();
        while (m.find()) {
            int startIndex = m.start();
            int stopIndex = m.end();

            for (int i = startIndex + 2; i < stopIndex - 2; i++) {
                if (!Character.isSpaceChar(charArray[i]) && !Character.isWhitespace(charArray[i]))
                    charArray[i] = '#';
            }
        }

        ret = String.valueOf(charArray);

        p = Pattern.compile("(?://.*)");
        m = p.matcher(ret);

        charArray = ret.toCharArray();
        while (m.find()) {
            int startIndex = m.start();
            int stopIndex = m.end();

            for (int i = startIndex + 2; i < stopIndex; i++) {
                if (!Character.isSpaceChar(charArray[i]) && !Character.isWhitespace(charArray[i]))
                    charArray[i] = '#';
            }
        }

        ret = String.valueOf(charArray);

        return ret;
    }

    public static void writeSnapshotsToFile(Collection<Snapshot> snapshots, Path filePath) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter writer = new FileWriter(filePath.toFile());
            gson.toJson(snapshots, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error("Could not write JSON file!", e);
            throw e;
        }
    }


    public static void CreateOutputFolder(String folderName) {
        File resultsFolder = new File("results" + File.separator + DepinderConfiguration.getInstance().getProjectID() + File.separator + folderName);
        if (!resultsFolder.exists())
            //noinspection ResultOfMethodCallIgnored
            resultsFolder.mkdirs();
    }

    @NotNull
    public static List<Commit> getCommitsInChronologicalOrder(Dependency dependency) {
        return dependency.getDepinderResults().stream()
                .map(DepinderResult::getCommit)
                .sorted(Comparator.comparing(Commit::getAuthorTimestamp))
                .collect(Collectors.toList());
    }
}
