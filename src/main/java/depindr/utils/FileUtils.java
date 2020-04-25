package depindr.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import depindr.model.MixTechnologySnapshot;
import depindr.model.TechnologySnapshot;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileUtils {

    public static String removeComments(String fileContent) {
        String ret = fileContent;

        Pattern p = Pattern.compile("(\\/\\*([\\S\\s]+?)\\*\\/)");
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

        p = Pattern.compile("(?:\\/\\/.*)");
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

    public static void writeSnapshotsToFile(Set<TechnologySnapshot> snapshots, Path filePath) throws IOException {
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


    public static void writeSnapshotsToFile(Collection<MixTechnologySnapshot> snapshots, Path filePath) throws IOException {
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
}
