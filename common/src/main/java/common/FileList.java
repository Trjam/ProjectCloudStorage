package common;

import javafx.collections.ObservableList;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileList {
    String fileName;
    String fileType;
    long fileSize;
}


