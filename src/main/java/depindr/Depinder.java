package depindr;


import depindr.configuration.DepinderConfiguration;
import depindr.constants.DepinderConstants;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
public class Depinder {
    public static void main(String[] args) {
        System.out.print("Reading configuration file: ");

        Path configurationFilePath = Paths.get(DepinderConstants.CONFIGURATION_FOLDER, DepinderConstants.CONFIGURATION_FILE);

        File resultsFolder = new File(DepinderConstants.RESULTS_FOLDER);
        if (!resultsFolder.exists())
            resultsFolder.mkdirs();

        Properties properties = new Properties();
        try {
            properties.load(new FileReader(configurationFilePath.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DepinderConfiguration.loadProperties(properties);

        String rootFolder = DepinderConfiguration.getInstance().getProperty(DepinderConstants.ROOT_FOLDER);

        log.info("\nroot folder: " + rootFolder);
    }

}
