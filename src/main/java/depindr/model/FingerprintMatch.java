package depindr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FingerprintMatch {
    private String matchContent;
    private int startLine;
    private int endLine;
    private int startIndex;
    private int endIndex;
}
