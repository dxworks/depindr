package depindr.analyzers;

import depindr.DepinderResult;
import depindr.json.Dependency;
import depindr.model.Commit;
import depindr.model.CommitRegistry;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DisappearanceOfATechAnalyzer {

    public void didATechDisappear(Dependency dependency, CommitRegistry commitRegistry) {
        Map<ZonedDateTime, List<Commit>> commitsPerDay = commitRegistry.getAll().stream().collect(Collectors.groupingBy(commit -> commit.getAuthorTimestamp().truncatedTo(ChronoUnit.DAYS)));
        Map<ZonedDateTime, Integer> dayContainsDependency = commitsPerDay.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        entry.getValue().stream()
                                .mapToInt(commit -> commit.getResults().stream()
                                        .filter(depinderResult -> depinderResult.getDependency().equals(dependency))
                                        .mapToInt(DepinderResult::getValue).sum()
                                ).max().orElse(0)));


        Map<Commit, List<DepinderResult>> resultsByCommitId = dependency.getResults().stream()
                .collect(Collectors.groupingBy(DepinderResult::getCommit));
        AtomicBoolean techExistedBefore = new AtomicBoolean(false);

//        resultsByCommitId.entrySet().stream()
//                .map(commitListEntry -> {
//                    int numberOfAppearances = commitListEntry.getValue().size();
//                    if(numberOfAppearances == 0){
//                        techExistedBefore.set(true);
//                    }
//
//                })
    }
}
