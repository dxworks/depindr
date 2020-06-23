package depindr.analyzers;

import depindr.Depinder;

import java.util.Arrays;
import java.util.List;

public interface DepinderCommand {
    String APPEARANCE = "--appearance";
    String SPREAD = "--spread";
    String MIX = "--mix";
    String AUTHORS = "--authors";
    String AUTHORS_KNOWLEDGE = "--authors_knowledge";
    List<String> VERSION = Arrays.asList("version", "-version", "--version", "-v");
    List<String> HELP = Arrays.asList("help", "-help", "--help", "-h");

    boolean parse(String[] args);

    String usage();

    void execute(Depinder depinder, String[] args);
}
