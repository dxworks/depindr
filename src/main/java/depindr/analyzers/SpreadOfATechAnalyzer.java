package depindr.analyzers;

import depindr.Depinder;
import depindr.DepinderFile;
import depindr.DepinderResult;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SpreadOfATechAnalyzer implements DepinderCommand {
    //at all commits

    public void execute(Depinder depinder, String[] args) {
        String tech = args[2];
        AtomicInteger commitIndex = new AtomicInteger(1);
        depinder.getCommitRegistry().getAll().forEach(commit -> {
            depinder.getDependencyRegistry().getAll().forEach(dependency -> {
                List<String> filesWithCertainTech = dependency.getDepinderResults().stream()
                        .filter(depinderResult -> depinderResult.getCommit().equals(commit))
                        .map(DepinderResult::getDepinderFile)
                        .map(DepinderFile::getName)
                        .filter(s -> dependency.getName().equals(tech))
                        .collect(Collectors.toList());

                if (!filesWithCertainTech.isEmpty()) {
                    System.out.println("Commit no: " + commitIndex.getAndIncrement());
                    System.out.printf("Technology %s is spread in %d files \n", dependency.getName(), filesWithCertainTech.size());
                    System.out.printf("%s: has %s \n", dependency.getName(), filesWithCertainTech.toString());
                }

            });
        });

    }

    @Override
    public boolean parse(String[] args) {
        return args.length == 4;
    }

    @Override
    public String usage() {
        return "depinder --spread <json_output_name_for_results> <technology_name> <flag_for_removal_of_comments>";
    }
}
