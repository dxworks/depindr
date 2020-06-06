package depindr.model.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@Builder
public class TechnologySnapshot {
    private ZonedDateTime snapshotTimestamp;
    private int numberOfFiles;
    private int usageOfTechnology;
    private String commitID;
}
