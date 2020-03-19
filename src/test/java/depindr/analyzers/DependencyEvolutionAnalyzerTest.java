package depindr.analyzers;

import depindr.Depinder;
import depindr.json.Dependency;
import depindr.model.DependencyRegistry;
import depindr.model.TechnologySnapshot;
import org.junit.Test;

import java.util.Set;

public class DependencyEvolutionAnalyzerTest {

    DependencyEvolutionAnalyzer dependencyEvolutionAnalyzer;

    @Test
    public void testDependencyEvolutionOnJavaComplete() {
        Depinder depinder = new Depinder("path/to/dependency/file");

        depinder.analyzeProject("path", "master", "true");

        DependencyRegistry dependencyRegistry = depinder.getDependencyRegistry();

        String dependencyID = "Java Util";
        Dependency dependency = dependencyRegistry.getById(dependencyID)
                .orElseThrow(() -> new IllegalArgumentException(dependencyID));

        Set<TechnologySnapshot> snapshots = dependencyEvolutionAnalyzer.evolutionOfATech(dependency);
    }
}