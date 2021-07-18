package com.gb.trjamich.project.cloudstorage.classes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Request {
    String reqType;
    String operation;
    String srcPath;
    String targetPath;
    User user;
}
