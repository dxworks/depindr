package depindr.analyzers;

import depindr.Depinder;

public class AuthorKnowledge implements DepinderCommand {
    @Override
    public boolean parse(String[] args) {
        return args.length == 3;
    }

    @Override
    public String usage() {
        return "depinder --authors_knowledge <json_output_name_for_results> <flag_for_removal_of_comments>";
    }

    @Override
    public void execute(Depinder depinder, String[] args) {

    }
}
