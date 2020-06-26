package depindr.analyzers;

import depindr.Depinder;

import java.util.ArrayList;
import java.util.List;

public class AllCommands implements DepinderCommand {
    private final List<DepinderCommand> commandList = new ArrayList<>();

    @Override
    public boolean parse(String[] args) {
        return args.length == 7;
    }

    @Override
    public String usage() {
        return "depinder --all <appearance.json> <spread.json> <mix.json> <integer threshold for mix> <authors.json> <flag_for_removal_of_comments>";
    }

    @Override
    public void execute(Depinder depinder, String[] args) {
        commandList.add(new AppearanceOfATechAnalyzer());
        commandList.add(new AuthorKnowledge());
        commandList.add(new SpreadOfATechAnalyzer());
        commandList.add(new FileMixTechnologyAnalyzer());

        commandList.forEach(depinderCommand -> depinderCommand.execute(depinder, args));

    }
}
