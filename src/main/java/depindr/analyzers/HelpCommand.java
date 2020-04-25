package depindr.analyzers;

import depindr.Depinder;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HelpCommand implements DepinderCommand {
    @Override
    public boolean parse(String[] args) {
        if (args.length != 1)
            return false;

        return HELP.contains(args[0]);
    }

    public void execute(Depinder depinder, String[] args) {
        String usage = "Depinder  -  usage guide:\n";
        usage += "Configure the source root and the project id in the config/depinder-conf.properties file\n\n";

        usage += "This is a list of the commands:\n";

        usage += Stream.of(
                new HelpCommand(),
                new VersionCommand(),
                new AppearanceOfATechAnalyzer(),
                new SpreadOfATechAnalyzer(),
                new FileMixTechnologyAnalyzer())
//                new ValueOfTechForEachCommit())
                .map(DepinderCommand::usage)
                .map(s -> "\t" + s)
                .collect(Collectors.joining("\n"));

        usage += "\n\nPlease run insider with the specified commands from the folder you have installed DepindR to!\n";

        System.out.println(usage);
    }

    @Override
    public String usage() {
        return "depinder {-h | -help | --help | help}";
    }
}
