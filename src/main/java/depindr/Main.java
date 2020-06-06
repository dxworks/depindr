package depindr;

import depindr.analyzers.*;
import depindr.configuration.DepinderConfiguration;
import depindr.constants.DepinderConstants;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static depindr.analyzers.DepinderCommand.*;

/*
 * #DONE 0.Read configuration file
 * #DONE 1.Read dependencies from .json file and print on console the file
 * #DONE 2. Read repository using JGit (checkout commits, read al files, create commit object, match Dependencies on all files)
 * */

public class Main {

    private static final HelpCommand helpCommand = new HelpCommand();
    private static final VersionCommand versionCommand = new VersionCommand();

    public static void main(String[] args) {

        if (args == null) {
            System.err.println("Arguments cannot be null");
            return;
        }

        if (args.length == 0) {
            System.err.println("No command found");
            helpCommand.execute(null, args);
            return;
        }

        if (versionCommand.parse(args)) {
            System.out.println("Chose version command\n");
            versionCommand.execute(null, args);
            return;
        }

        if (helpCommand.parse(args)) {
            System.out.println("Chose help command\n");
            helpCommand.execute(null, args);
            return;
        }

        String command = args[0];
        boolean removeCommentsFlag = Boolean.parseBoolean(args[args.length - 1]);
        DepinderCommand depinderCommand = getDepinderCommand(command);

        if (depinderCommand == null) {
            System.err.println("depinderCommand == null!!!\nInvalid command!\n");
            helpCommand.execute(null, args);
            return;
        }

        boolean isValidInput = depinderCommand.parse(args);
        if (!isValidInput) {
            System.err.println("Input is not valid!");
            helpCommand.execute(null, args);
            return;
        }

        File resultsFolder = new File(DepinderConstants.RESULTS_FOLDER);
        if (!resultsFolder.exists())
            //noinspection ResultOfMethodCallIgnored
            resultsFolder.mkdirs();
        String rootFolderOfAnalyzedProject = readDepinderConfiguration();
        String branchName = DepinderConfiguration.getInstance().getProperty(DepinderConstants.BRANCH);

        Depinder depinder = new Depinder(DepinderConfiguration.getInstance().getProperty(DepinderConstants.JSON_FINGERPRINT_FILES));
        depinder.analyzeProject(rootFolderOfAnalyzedProject, branchName, removeCommentsFlag);

        depinderCommand.execute(depinder, args);

        System.out.println("DepindR finished analysis.");

    }

    private static String readDepinderConfiguration() {
        Path configurationFilePath = Paths.get(DepinderConstants.CONFIGURATION_FOLDER, DepinderConstants.CONFIGURATION_FILE);

        DepinderConfiguration.getInstance().loadProperties(configurationFilePath.toFile());

        return DepinderConfiguration.getInstance().getProperty(DepinderConstants.ROOT_FOLDER);
    }

    private static DepinderCommand getDepinderCommand(String command) {
        switch (command) {
            case APPEARANCE:
                return new AppearanceOfATechAnalyzer();
            case SPREAD:
                return new SpreadOfATechAnalyzer();
            case MIX:
                return new FileMixTechnologyAnalyzer();
            default:
                return null;
        }
    }
}
