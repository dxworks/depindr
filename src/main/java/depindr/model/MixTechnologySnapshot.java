package depindr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MixTechnologySnapshot {
    private ZonedDateTime snapshotTimestamp;
    private String commitID;
    private List<MixTechFile> files;
}
