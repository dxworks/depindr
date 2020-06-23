package depindr.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDependencyKnowledgeDTO {
    private String authorName;
    private String authorEmail;
    private Map<String, Integer> dependencyKnowledge;
}
