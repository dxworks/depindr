package depindr.analyzers;

//public class ValueOfTechForEachCommit implements DepinderCommand {
//
//    public Set<TechnologySnapshot> dependencyValueForCommits(Dependency dependency) {
//        Map<Commit, List<DepinderResult>> resultsByCommitId = dependency.getResults().stream()
//                .collect(Collectors.groupingBy(DepinderResult::getCommit));
//
//        return resultsByCommitId.entrySet().stream()
//                .map(entry -> {
//                    int count = entry.getValue().size();
//                    System.out.printf("Commit: %s technology %s is spread in %d  files \n", entry.getKey().getID(), dependency.getName(), count);
//
//                    return TechnologySnapshot.builder()
//                            .commitID(entry.getKey().getID())
//                            .snapshotTimestamp(entry.getKey().getAuthorTimestamp())
//                            .numberOfFiles(count)
//                            .usageOfTechnology(entry.getValue().size())
//                            .build();
//                })
//                .collect(Collectors.toSet());
//    }
//
//    @Override
//    public boolean parse(String[] args) {
//        return false;
//    }
//
//    @Override
//    public String usage() {
//        return "depinder find <path_to_json>...";
//    }
//}
