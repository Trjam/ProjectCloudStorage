package com.gb.trjamich.project.cloudstorage.utils;

import com.google.gson.Gson;
import common.FileList;
import common.Request;
import common.Response;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static com.gb.trjamich.project.cloudstorage.handlers.NavigateHandler.serverRoot;

public class HandlerUtils {

    public HandlerUtils() {
    }

    public Response authLoginOkResponse(Request request, UUID uuid) {
        return Response.builder()
                .reqType(request.getReqType())
                .operation(request.getOperation())
                .status("ok")
                .token(uuid)
                .build();
    }

    public Response authOkResponse(Request request) {
        return Response.builder()
                .reqType(request.getReqType())
                .operation(request.getOperation())
                .status("ok")
                .build();
    }

    public Response faultResponse(String info, Request request) {
        return Response.builder()
                .reqType(request.getReqType())
                .operation(request.getOperation())
                .status("fault")
                .info(info)
                .build();
    }

    public Response navOkResponse(Request request, List<FileList> list, Path currentServerPath){
        return Response.builder()
                .reqType(request.getReqType())
                .operation(request.getOperation())
                .list(list)
                .cSP(currentServerPath.toString()
                        .replace(Path.of(serverRoot.toString(), request.getUser().getLogin()).toString(),"home"))
                .status("ok")
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
        ctx.write(msg+"\r\n");
        ctx.flush();
    }

    public Path getReqPath (Request request) {
        return Path.of(request.getPath()
                .replace("home", serverRoot.toString() + File.separator + request.getUser().getLogin()));
    }

    public Path getReqTrgPath (Request request) {
        return Path.of(request.getTargetPath()
                .replace("home", serverRoot.toString() + File.separator + request.getUser().getLogin()));
    }

    public Path getReqSrcPath(Request request) {
        return Path.of(request.getSrcPath()
                .replace("home", serverRoot.toString() + File.separator + request.getUser().getLogin()));
    }
}