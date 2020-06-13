package depindr.model.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MixTechnologySnapshot implements Snapshot {
    private String snapshotTimestamp;
    private String commitID;
    private List<MixTechFile> files;
}
