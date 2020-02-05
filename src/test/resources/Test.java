package org.dxworks.dxplatform.plugins.insider;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


@Slf4j
public class Insider {

    private static HelpCommand helpCommand = new HelpCommand();
    private static VersionCommand versionCommand = new VersionCommand();

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
            versionCommand.execute(null, args);
            return;
        }

        if (helpCommand.parse(args)) {
            helpCommand.execute(null, args);
            return;
        }

        String command = args[0];

        InsiderCommand insiderCommand = getInsiderCommand(command);

        if (insiderCommand == null) {
            System.err.println("Invalid command!");
            helpCommand.execute(null, args);
            return;
        }

        boolean isValidInput = insiderCommand.parse(args);
        if (!isValidInput) {
            log.error("Input is not valid!");
            helpCommand.execute(null, args);
            return;
        }

        if (insiderCommand instanceof NoFilesCommand) {
            insiderCommand.execute(null, args);
        } else {
            List<InsiderFile> insiderFiles = readInsiderConfiguration();
            insiderCommand.execute(insiderFiles, args);
        }

        System.out.println("Insider 1.0 finished analysis");
    }
}