package depindr.model.dto;

import depindr.model.snapshot.Snapshot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDependencyKnowledgeDTO implements Snapshot {
    private String authorName;
    private String authorEmail;
    private Map<String, Integer> dependencyKnowledge;
}
