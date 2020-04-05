package depindr.analyzers;

import depindr.DepinderFile;
import depindr.DepinderResult;
import depindr.model.Commit;
import depindr.model.CommitRegistry;
import depindr.model.DependencyRegistry;

import java.util.List;
import java.util.stream.Collectors;

public class SpreadnessOfATechAnalyzer {
    //#TODO maybe would be usefull to know at a certain commit, now it's by default at the last commit
    public static void technologyWithinFiles(CommitRegistry commitRegistry, DependencyRegistry dependencyRegistry, String tech) {
        Commit lastCommit = commitRegistry.getLastCommit().get();
        dependencyRegistry.getAll().forEach(dependency -> {
            List<String> filesWithCertainTech = dependency.getDepinderResults().parallelStream()
                    .filter(depinderResult -> depinderResult.getCommit().equals(lastCommit))
                    .map(DepinderResult::getDepinderFile)
                    .map(DepinderFile::getName)
                    .filter(s -> dependency.getName().equals(tech))
                    .collect(Collectors.toList());

            if (!filesWithCertainTech.isEmpty()) {
                System.out.printf("Technology %s is spread in %d files \n", dependency.getName(), filesWithCertainTech.size());
                System.out.printf("%s: has %s \n", dependency.getName(), filesWithCertainTech.toString());
            }
        });
    }
}
