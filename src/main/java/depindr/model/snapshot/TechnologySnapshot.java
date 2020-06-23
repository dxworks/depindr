package depindr.model.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class TechnologySnapshot implements Snapshot {
    private List<String> authors;
    private int nrAuthors;
    private String snapshotTimestamp;
    private int numberOfFiles;
    private int value;
    private List<String> files;
    private String commitID;
    private String techId;
}
