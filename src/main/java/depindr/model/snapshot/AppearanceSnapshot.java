package depindr.model.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@Builder
public class AppearanceSnapshot implements Snapshot {
    private ZonedDateTime snapshotTimestamp;
    private String commitID;
    private String nameOfDependency;
    private String authorName;

}
