package com.gb.trjamich.project.cloudstorage.handlers;

import com.gb.trjamich.project.cloudstorage.utils.HandlerUtils;
import common.FileList;
import common.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class NavigateHandler extends ChannelInboundHandlerAdapter {
    public static final ConcurrentLinkedDeque<SocketChannel> channels = new ConcurrentLinkedDeque<>();
    private final HandlerUtils utils = new HandlerUtils();
    public Path currentServerPath = Path.of("server/serverDir");
    public static Path serverRoot = Path.of("server/serverDir");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("enter nav h");

        Request request = (Request) msg;

        if (request.getReqType().equals("nav")) {
            if (request.getOperation().equals("getFileList")) {
                currentServerPath = Path.of(serverRoot.toString(), request.getUser().getLogin());
                if (!Files.exists(currentServerPath)) {
                    try {
                        Files.createDirectory(currentServerPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (request.getTargetPath() != null) {
                    currentServerPath= utils.getReqTrgPath(request);
                }
                utils.sendResponse(ctx, utils.navOkResponse(request, getFileList(currentServerPath), currentServerPath));

            } else if (request.getOperation().equals("cd")) {
                if (Files.exists(utils.getReqTrgPath(request))) {
                    currentServerPath = utils.getReqTrgPath(request);
                    utils.sendResponse(ctx, utils.navOkResponse(request, getFileList(currentServerPath), currentServerPath));
                }else{
                    utils.sendResponse(ctx, utils.faultResponse("Something go wrong", request));
                }
            } else if (request.getOperation().equals("mkDir")) {
                if (!Files.exists(utils.getReqTrgPath(request))) {
                    try {
                        Files.createDirectory(utils.getReqTrgPath(request));
                        currentServerPath= utils.getReqPath(request);
                        utils.sendResponse(ctx, utils.navOkResponse(request, getFileList(currentServerPath), currentServerPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    utils.sendResponse(ctx, utils.faultResponse("Directory with such name already exist", request));
                }
            } else if (request.getOperation().equals("del")) {
                if (Files.exists(utils.getReqTrgPath(request))) {
                    try {
                        Files.delete(utils.getReqTrgPath(request));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    currentServerPath= utils.getReqPath(request);
                    utils.sendResponse(ctx, utils.navOkResponse(request, getFileList(currentServerPath), currentServerPath));
                } else {
                    utils.sendResponse(ctx, utils.faultResponse("No such file or directory", request));
                }
            } else if (request.getOperation().equals("ren")) {
                if (Files.exists(utils.getReqSrcPath(request))) {
                    try {
                        System.out.println(utils.getReqSrcPath(request));
                        System.out.println(utils.getReqTrgPath(request));
                        Files.move(utils.getReqSrcPath(request), utils.getReqTrgPath(request), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    currentServerPath= utils.getReqPath(request);
                    utils.sendResponse(ctx, utils.navOkResponse(request, getFileList(currentServerPath), currentServerPath));
                } else {
                    utils.sendResponse(ctx, utils.faultResponse("No such file or directory", request));
                }
            }

        } else {
            ctx.fireChannelRead(request);
        }
    }

    public static List<FileList> getFileList(Path path) {
        File[] files = new File(path.toString()).listFiles();

        ObservableList<FileList> list = FXCollections.observableArrayList();

        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                list.add(FileList.builder()
                        .fileName(file.getName())
                        .fileType("Directory")
                        .build());
            } else {
                list.add(FileList.builder()
                        .fileName(file.getName())
                        .fileType("File")
                        .fileSize(file.length() / 1024)
                        .build());
            }
        }
        FXCollections.sort(list, Comparator.comparing(FileList::getFileType));
        if (!path.getParent().equals(serverRoot)) {
            list.add(0, FileList.builder()
                    .fileName("...")
                    .fileType("Go to parent directory")
                    .build());
        }
        List<FileList> arr = list;
        return arr;
    }

}
