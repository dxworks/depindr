package depindr.analyzers;

import depindr.DepinderResult;
import depindr.json.Dependency;
import depindr.model.Commit;
import depindr.model.TechnologySnapshot;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyEvolutionAnalyzer {

    //TODO: move this to master
    //TODO: create a test for a demo repository and check the results are as expected
    // steps. Create a test, run Depindr on the test repository, get the results and serialize them into a JSON file.
    // Add the json file to test/resources.
    // the test should test that the result is equal to the results from the JSON file.
    public Set<TechnologySnapshot> evolutionOfATech(Dependency dependency) {
        Map<Commit, List<DepinderResult>> resultsByCommitId = dependency.getResults().stream()
                .collect(Collectors.groupingBy(DepinderResult::getCommit));

        return resultsByCommitId.entrySet().stream()
                .map(entry -> TechnologySnapshot.builder()
                        .commitID(entry.getKey().getID())
                        .snapshotTimestamp(entry.getKey().getAuthorTimestamp())
                        .numberOfFiles((int) entry.getValue().stream().map(DepinderResult::getFile).distinct().count())
                        .usageOfTechnology(entry.getValue().stream().mapToInt(DepinderResult::getValue).sum())
                        .build())
                .collect(Collectors.toSet());
    }
}
