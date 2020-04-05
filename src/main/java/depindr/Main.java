package depindr;

import depindr.analyzers.ValueOfTechForEachCommit;
import depindr.configuration.DepinderConfiguration;
import depindr.constants.DepinderConstants;
import depindr.exceptions.DepinderException;
import depindr.model.TechnologySnapshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static depindr.utils.FileUtils.writeSnapshotsToFile;

/*
 * #DONE 0.Read configuration file
 * #DONE 1.Read dependencies from .json file and print on console the file
 * #DONE 2. Read repository using JGit (checkout commits, read al files, create commit object, match Dependencies on all files)
 * */

public class Main {

    public static void main(String[] args) {
        System.out.print("Reading configuration file: \n");

        Path configurationFilePath = Paths.get(DepinderConstants.CONFIGURATION_FOLDER, DepinderConstants.CONFIGURATION_FILE);

        File resultsFolder = new File(DepinderConstants.RESULTS_FOLDER);
        if (!resultsFolder.exists())
            resultsFolder.mkdirs();

        DepinderConfiguration.getInstance().loadProperties(configurationFilePath.toFile());

        String rootFolderOfAnalyzedProject = DepinderConfiguration.getInstance().getProperty(DepinderConstants.ROOT_FOLDER);
        String branchName = DepinderConfiguration.getInstance().getProperty(DepinderConstants.BRANCH);

        //#todo maybe this should be transmitted in the future as a param to main
        boolean removeCommentsFlag = false;

        Depinder depinder = new Depinder(DepinderConfiguration.getInstance().getProperty(DepinderConstants.JSON_FINGERPRINT_FILES));

        depinder.analyzeProject(rootFolderOfAnalyzedProject, branchName, removeCommentsFlag);
//
//        //Starts analyzing stuff
//
//        FileMixTechnologyAnalyzer fileMixTechnologyAnalyzer = new FileMixTechnologyAnalyzer();
//        //#TODO verify writeSnapshots still works after changing type of first param
//        //#TODO test fileMixTech works and find a way to run this with command line args
//
//        List<MixTechnologySnapshot> snapshots = fileMixTechnologyAnalyzer.analyze(depinder.getCommitRegistry(), 2);


//        AppearanceOfATechAnalyzer appearanceOfATechAnalyzer = new AppearanceOfATechAnalyzer();
//
//        appearanceOfATechAnalyzer.whenDidATechAppear(depinder.getDependencyRegistry());

        ValueOfTechForEachCommit dependencyEvolutionAnalyzer = new ValueOfTechForEachCommit();

        Set<TechnologySnapshot> snapshots = dependencyEvolutionAnalyzer.dependencyValueForCommits(depinder.getDependencyRegistry().getById(String.join(", ", "Java Util", "External Libraries")).orElseThrow(IllegalArgumentException::new), depinder.getCommitRegistry());

        Path filePath = Paths.get(resultsFolder.getName() + "\\Evolution_Of_Tech_Results2.json");

//        Path filePath = Paths.get(resultsFolder.getName()+ "\\File_Mix_Results.json");
        try {
            writeSnapshotsToFile(snapshots, filePath);
        } catch (IOException e) {
            throw new DepinderException("Could not write snapshots to file.", e);
        }

        System.out.println("gata");

    }
}
