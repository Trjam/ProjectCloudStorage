package com.gb.trjamich.project.cloudstorage.utils;

import com.gb.trjamich.project.cloudstorage.classes.Request;
import com.gb.trjamich.project.cloudstorage.classes.Response;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;

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

    public void sendResponse(ChannelHandlerContext ctx, Response response) {
        Gson g = new Gson();
        String msg = g.toJson(response, Response.class);
        ctx.writeAndFlush(msg);
    }
}