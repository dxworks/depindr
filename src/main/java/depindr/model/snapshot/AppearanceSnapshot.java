package depindr.model.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AppearanceSnapshot implements Snapshot {
    private String snapshotTimestamp;
    private String commitID;
    private String nameOfDependency;
    private String authorName;

}
