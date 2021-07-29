package common;

import javafx.collections.ObservableList;
import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Response {
    String reqType;
    String operation;
    String status;
    String info;
    UUID token;
    List<FileList> list;
    String cSP;
}
