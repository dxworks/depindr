package depindr.analyzers;

import depindr.Depinder;

public class VersionCommand implements DepinderCommand {
    @Override
    public boolean parse(String[] args) {
        if (args.length != 1)
            return false;

        return VERSION.contains(args[0]);
    }

    public void execute(Depinder depinder, String[] args) {
        String version = "DepindR 1.0.0";

        System.out.println(version);
    }

    @Override
    public String usage() {
        return "depinder {-v | -version | --version | version}";
    }
}
