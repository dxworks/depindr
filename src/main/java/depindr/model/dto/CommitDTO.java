package depindr.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommitDTO {
    private String commitID;
    private AuthorDTO author;
    private AuthorDTO committer;
    private ZonedDateTime authorTimestamp;
    private ZonedDateTime committerTimestamp;
    private CommitMessageDTO message;
    private String[] parentIDs;
}
