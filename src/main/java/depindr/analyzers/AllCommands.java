package depindr.analyzers;

import depindr.Depinder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AllCommands implements DepinderCommand {
    private final List<DepinderCommand> commandList = new ArrayList<>();

    @Override
    public boolean parse(String[] args) {
        try {
            Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            System.out.println("Wrong threshold format! Please provide integer value\n");
            return false;
        }
        Pattern pattern = Pattern.compile("\\.json$");

        Matcher matcher1 = pattern.matcher(args[1]);
        Matcher matcher2 = pattern.matcher(args[2]);
        Matcher matcher3 = pattern.matcher(args[3]);
        Matcher matcher5 = pattern.matcher(args[5]);

        if (args.length != 7)
            return false;
        if (!matcher1.find()) {
            System.out.println("File format not supported. Please provide a <appearance.json> file name!");
            return false;
        }

        if (!matcher2.find()) {
            System.out.println("File format not supported. Please provide a <spread.json> file name!");
            return false;
        }

        if (!matcher3.find()) {
            System.out.println("File format not supported. Please provide a <mix.json> file name!");
            return false;
        }

        if (!matcher5.find()) {
            System.out.println("File format not supported. Please provide a <authors.json> file name!");
            return false;
        }

        if ((!args[6].matches("true")) && (!args[6].matches("false"))) {
            System.out.println("Flag not supported. Please provide true/false");
            return false;
        }

        return true;


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
