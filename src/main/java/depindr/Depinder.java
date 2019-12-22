package depindr;


import depindr.configuration.DepinderConfiguration;
import depindr.constants.DepinderConstants;
import depindr.json.Dependency;
import depindr.json.JsonConfigurationDTO;
import depindr.json.JsonFingerprintParser;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/*
* #DONE 0.Read configuration file
* #DONE 1.Read dependecies from .json file and print on console the file
* #TODO 2. Read repository using JGit (checkout commits, read al files, create commit object, match Dependencies on all files)
* */

@Slf4j
public class Depinder {
    public static void main(String[] args) {
        System.out.print("Reading configuration file: \n");

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

        String rootFolder = DepinderConfiguration.getInstance().getProperty(DepinderConstants.JSON_FINGERPRINT_FILES);

        log.info("Check to see it read correctly\n" + getPropertyAsString(properties));

        List<DepinderFile> depinderFiles = new ArrayList<>();
        try {
            List<Path> pathList = Files.walk(Paths.get(rootFolder)).collect(Collectors.toList());
            for(Path path : pathList){
                //log.debug("debug: " + path);
                depinderFiles.add(DepinderFile.builder()
                        .path(path.toAbsolutePath().toString())
                        .name(path.getFileName().toString())
                        .content(new String(Files.readAllBytes(path)))
                        .build());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // for debugging purposes
//        for (DepinderFile df : depinderFiles){
//            df.printInfo();
//
//        }

        testJsonParser();
    }

    public static void testJsonParser(){
        String rootFolder = DepinderConfiguration.getInstance().getProperty(DepinderConstants.JSON_FINGERPRINT_FILES);

        JsonFingerprintParser jsonFingerprintParser = new JsonFingerprintParser();
        List<Dependency> jsonDependencies = jsonFingerprintParser.parseTechnologiesFile(rootFolder);


        jsonDependencies.forEach(System.out::println);
    }

    @NotNull
    private static String getPropertyAsString(Properties prop) {
        StringWriter writer = new StringWriter();
        prop.list(new PrintWriter(writer));
        return writer.getBuffer().toString();
    }

}
