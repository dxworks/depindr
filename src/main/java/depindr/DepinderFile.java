package depindr;

import depindr.configuration.DepinderConfiguration;
import depindr.constants.DepinderConstants;
import depindr.model.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
@Builder
public class DepinderFile implements Entity<String> {
    private String name, path, content, extension;
    private List<Integer> lineBreaks;
    private int lines;

    private List<DepinderResult> results = new ArrayList<>();

    public DepinderFile(){
        extractLineBreaks(content);
        lines = lineBreaks.size();
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

    void printInfo(){
        log.info("\nContains: " + name + path + content);
    }

    public void addResults(List<DepinderResult> depinderResults) {
        results.addAll(depinderResults);
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
}
