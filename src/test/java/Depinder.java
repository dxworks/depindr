import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Depinder {
    public static void main(String[] args) throws IOException {
        String filePath = Depinder.class.getClassLoader().getResource("bla.txt").getFile();
        File file = new File(filePath);
        Files.readAllLines(Paths.get(file.getAbsolutePath())).forEach(System.out::println);
    }

}


