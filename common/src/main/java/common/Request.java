package common;

import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;
import java.util.UUID;

@Data
@Builder
public class Request {
    String reqType;
    String operation;
    String path;
    String srcPath;
    String targetPath;
    UUID token;
    User user;
    long fileLength;
}
