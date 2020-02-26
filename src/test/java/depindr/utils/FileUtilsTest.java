package depindr.utils;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class FileUtilsTest {

    @Test
    public void removeComments() throws IOException {
        String contentWithComments = getContentOfResourceFile("FileWithComments.java");
        String expectedContentWithoutComments = getContentOfResourceFile("FileWithoutComments.java");

        String computedContentWithoutComments = FileUtils.removeComments(contentWithComments);

        assertEquals(expectedContentWithoutComments, computedContentWithoutComments);
    }

    private String getContentOfResourceFile(String resourceFile) throws IOException {
        String filePathAsString = getClass().getClassLoader().getResource(resourceFile).getPath();
        File file = new File(filePathAsString);
        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

        return content;
    }
}