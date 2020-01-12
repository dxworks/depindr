package depindr.json;

import depindr.DepinderFile;
import depindr.DepinderResult;
import depindr.model.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
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

    public List<DepinderResult> analyze(DepinderFile depinderFile) {
        if (!accepts(depinderFile.getExtension()))
            return Collections.emptyList();

        List<DepinderResult> depRes = patterns.parallelStream()
                .map(pattern -> DepinderResult.builder()
                        .file(depinderFile.getFullyQualifiedName())
                        .category(category)
                        .name(name)
                        .value(getPatternOccurrencesInFile(depinderFile, pattern))
                        .dependency(this)
                        .depinderFile(depinderFile)
                        .build())

                .collect(Collectors.toList());

        depinderResults.addAll(depRes);
        depinderFile.addResults(depRes);

        return depRes;
    }


    private boolean accepts(String extension) {
        return languages.stream().anyMatch(language -> languageRegistry.isOfLanguage(language, extension))
                || extensions.contains(extension);
    }

    private int getPatternOccurrencesInFile(DepinderFile insiderFile, Pattern pattern) {
        int fileOcc = 0;
        Matcher matcher = pattern.matcher(insiderFile.getContent());

        while (matcher.find())
            fileOcc++;
        return fileOcc;
    }

    @Override
    public String getID() {
        return String.join(", ", name, category);
    }
}
