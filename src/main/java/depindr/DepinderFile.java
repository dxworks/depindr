package depindr;

import depindr.configuration.DepinderConfiguration;
import depindr.constants.DepinderConstants;
import depindr.model.Entity;
import depindr.model.FingerprintMatch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@Slf4j
public class DepinderFile implements Entity<String> {
    private String name, path, content, extension;
    private List<Integer> lineBreaks;
    private int lines;

    private List<DepinderResult> results;

    public static DepinderFileBuilder builder() {
        return new DepinderFileBuilder();
    }

    public void init() { //aici bag treaba cu remove comments?
        extractLineBreaks(content);
        lines = lineBreaks.size();
        results = new ArrayList<>();
    }

    private void extractLineBreaks(String content) {
        lineBreaks = new ArrayList<>();
        lineBreaks.add(-1);
        for (int i = 0; i < content.length(); i++){
            if(content.charAt(i) == '\n'){
                lineBreaks.add(i);
            }
        }

    }

    protected int getLineNumberOfAbsoluteCharacterIndex(int index) {
        for (int i = 0; i < lineBreaks.size(); i++) {
            if (index <= lineBreaks.get(i))
                return i;
        }

        return lineBreaks.size();
    }

    @Override
    public String getID() {
        return getFullyQualifiedName();
    }

    public String getFullyQualifiedName() {
        return getPath()
                .substring(DepinderConfiguration.getInstance().getProperty(DepinderConstants.ROOT_FOLDER).length() + 1)
                .replace('\\', '/');
    }

    public Stream<FingerprintMatch> getMatchesInFile(Pattern pattern) {
        Matcher matcher = pattern.matcher(content);

        List<FingerprintMatch> matches = new ArrayList<>();

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            FingerprintMatch fingerprintMatch = FingerprintMatch.builder()
                    .startIndex(start)
                    .endIndex(end)
                    .startLine(getLineNumberOfAbsoluteCharacterIndex(start))
                    .endLine(getLineNumberOfAbsoluteCharacterIndex(end))
                    .matchContent(content.substring(start, end))
                    .build();

            matches.add(fingerprintMatch);
        }
        return matches.stream();
    }

    public void addResult(DepinderResult depinderResult) {
        results.add(depinderResult);
    }

    public static class DepinderFileBuilder {
        private String name;
        private String path;
        private String content;
        private String extension;
        private List<Integer> lineBreaks;
        private int lines;
        private List<DepinderResult> results;

        DepinderFileBuilder() {
        }

        public DepinderFileBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DepinderFileBuilder path(String path) {
            this.path = path;
            return this;
        }

        public DepinderFileBuilder content(String content) {
            this.content = content;
            return this;
        }

        public DepinderFileBuilder extension(String extension) {
            this.extension = extension;
            return this;
        }

        public DepinderFileBuilder lineBreaks(List<Integer> lineBreaks) {
            this.lineBreaks = lineBreaks;
            return this;
        }

        public DepinderFileBuilder lines(int lines) {
            this.lines = lines;
            return this;
        }

        public DepinderFileBuilder results(List<DepinderResult> results) {
            this.results = results;
            return this;
        }

        public DepinderFile build() {
            DepinderFile depinderFile = new DepinderFile(name, path, content, extension, lineBreaks, lines, results);
            depinderFile.init();
            return depinderFile;
        }

        public String toString() {
            return "DepinderFile.DepinderFileBuilder(name=" + this.name + ", path=" + this.path + ", content=" + this.content + ", extension=" + this.extension + ", lineBreaks=" + this.lineBreaks + ", lines=" + this.lines + ", results=" + this.results + ")";
        }
    }
}
