package depindr.analyzers;

import depindr.DepinderResult;
import depindr.model.Commit;
import depindr.model.DependencyRegistry;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AppearanceOfATechAnalyzer {

    public void whenDidATechAppear(DependencyRegistry dependencyRegistry) {
        dependencyRegistry.getAll().forEach(dependency -> {
            List<Commit> commits = dependency.getDepinderResults().stream()
                    .map(DepinderResult::getCommit)
                    .sorted(Comparator.comparing(Commit::getAuthorTimestamp))
                    .collect(Collectors.toList());
            commits.stream().findFirst().ifPresent(commit ->
                    System.out.printf("Dependency %s appeared in commit %s on %s%n", dependency.getName(), commit.getID(), commit.getAuthorTimestamp().toString())
            );
        });
    }

}
