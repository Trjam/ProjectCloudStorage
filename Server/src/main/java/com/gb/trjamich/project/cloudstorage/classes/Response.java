package com.gb.trjamich.project.cloudstorage.classes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    String reqType;
    String operation;
    String status;
    String info;
}
