package depindr.json;

import depindr.DepinderFile;
import depindr.DepinderResult;
import depindr.model.dto.FingerprintMatch;
import depindr.model.entity.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class Dependency implements Entity<String> {

    private final LanguageRegistry languageRegistry = LanguageRegistry.getInstance();
    private String category;
    private String name;
    private List<String> languages;
    private List<String> extensions;
    private List<String> fingerprints;
    private List<Pattern> patterns;

    private List<DepinderResult> depinderResults = new ArrayList<>();

    public Dependency(String category, String name, List<String> languages, List<String> extensions,
                      List<String> fingerprints) {
        this.category = category;
        this.name = name;
        this.languages = languages;
        this.extensions = extensions;
        this.fingerprints = fingerprints;
        patterns = fingerprints.stream().map(Pattern::compile).collect(Collectors.toList());
    }

    public DepinderResult analyze(DepinderFile depinderFile) {
        if (!accepts(depinderFile.getExtension()))
            return null;

        List<FingerprintMatch> fingerprintMatches = patterns.parallelStream()
                .flatMap(depinderFile::getMatchesInFile)
                .distinct()
                .collect(Collectors.toList());

        DepinderResult depinderResult = null;
        if (fingerprintMatches.size() > 0) {
            depinderResult = DepinderResult.builder()
                    .file(depinderFile.getFullyQualifiedName())
                    .category(category)
                    .name(name)
                    .value(fingerprintMatches.size())
                    .fingerprintMatches(fingerprintMatches)
                    .dependency(this)
                    .depinderFile(depinderFile)
                    .build();

            depinderResults.add(depinderResult);
            depinderFile.addResult(depinderResult);
        }
        return depinderResult;
    }

    private boolean accepts(String extension) {
        return languages.stream().anyMatch(language -> languageRegistry.isOfLanguage(language, extension))
                || extensions.contains(extension);
    }

    @Override
    public String getID() {
        return String.join(", ", name, category);
    }

    public List<DepinderResult> getResults() {
        return depinderResults;
    }

    public void addResult(DepinderResult depinderResult) {
        depinderResults.add(depinderResult);
    }
}
