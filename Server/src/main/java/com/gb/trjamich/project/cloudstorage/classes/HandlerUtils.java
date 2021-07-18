package com.gb.trjamich.project.cloudstorage.classes;

import com.google.gson.Gson;

public class HandlerUtils {

    public HandlerUtils() {
    }

    public Response authOkResponse(Request request) {
        return Response.builder()
                .reqType(request.getReqType())
                .operation(request.getOperation())
                .status("ok")
                .build();
    }

    public Response authFaultResponse(String info, Request request) {
        return Response.builder()
                .reqType(request.getReqType())
                .operation(request.getOperation())
                .status("fault")
                .info(info)
                .build();
    }

    public Request getRequest(Object msg) {
        String str = (String) msg;
        str = str.trim();
        Gson g = new Gson();
        return g.fromJson(str, Request.class);
    }
}