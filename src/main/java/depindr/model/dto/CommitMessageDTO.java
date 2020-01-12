package depindr.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommitMessageDTO {
    private String fullDescription;
    private String shortDescription;
}
