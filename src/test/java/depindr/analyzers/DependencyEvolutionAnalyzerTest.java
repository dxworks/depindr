package depindr.analyzers;

import depindr.Depinder;
import depindr.configuration.DepinderConfiguration;
import depindr.constants.DepinderConstants;
import depindr.model.entity.Dependency;
import depindr.model.entity.DependencyRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class DependencyEvolutionAnalyzerTest {

    private static final String ROOT_FOLDER = "C:\\Users\\Bogdan\\Desktop\\Java_Project\\javacomplete";
//    private ValueOfTechForEachCommit dependencyEvolutionAnalyzer = new ValueOfTechForEachCommit();

    // steps. Create a test, run Depindr on the test repository, get the results and serialize them into a JSON file.
    // Add the json file to test/resources.
    // the test should test that the result is equal to the results from the JSON file.
    @Test
    public void testDependencyEvolutionOnJavaComplete() {
        Depinder depinder = new Depinder("D:\\Dev\\licenta\\thisIsIt\\depinder\\src\\test\\resources\\testFingerprints.json");

        DepinderConfiguration.getInstance().setProperty(DepinderConstants.ROOT_FOLDER, ROOT_FOLDER);
        depinder.analyzeProject(ROOT_FOLDER, "master", false);

        DependencyRegistry dependencyRegistry = depinder.getDependencyRegistry();

        String dependencyID = "Java Util, External Libraries";
        Dependency dependency = dependencyRegistry.getById(dependencyID)
                .orElseThrow(() -> new IllegalArgumentException(dependencyID));

//        Set<TechnologySnapshot> snapshots = dependencyEvolutionAnalyzer.dependencyValueForCommits(dependency);
//
//        Path filePath = Paths.get("D:\\Dev\\licenta\\thisIsIt\\depinder\\src\\test\\resources\\test_results.json");
//        try {
//            writeSnapshotsToFile(snapshots, filePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

}