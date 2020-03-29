package depindr.analyzers;

import depindr.Depinder;
import depindr.configuration.DepinderConfiguration;
import depindr.constants.DepinderConstants;
import depindr.json.Dependency;
import depindr.model.DependencyRegistry;
import org.junit.Test;

public class DisappearanceOfATechAnalyzerTest {
    private static final String ROOT_FOLDER = "C:\\Users\\Bogdan\\Desktop\\Java_Project\\javacomplete";
    private DisappearanceOfATechAnalyzer disappearanceOfATechAnalyzer = new DisappearanceOfATechAnalyzer();

    @Test
    public void testDependencyEvolutionOnJavaComplete() {
        Depinder depinder = new Depinder("D:\\Dev\\licenta\\thisIsIt\\depinder\\src\\test\\resources\\testFingerprints.json");

        DepinderConfiguration.getInstance().setProperty(DepinderConstants.ROOT_FOLDER, ROOT_FOLDER);
        depinder.analyzeProject(ROOT_FOLDER, "master", false);

        DependencyRegistry dependencyRegistry = depinder.getDependencyRegistry();

        String dependencyID = "Java Util, External Libraries";
        Dependency dependency = dependencyRegistry.getById(dependencyID)
                .orElseThrow(() -> new IllegalArgumentException(dependencyID));

        disappearanceOfATechAnalyzer.didATechDisappear(dependency, depinder.getCommitRegistry());


    }

}