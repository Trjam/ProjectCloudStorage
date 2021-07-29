package com.gb.trjamich.project.cloudstorage.handlers;

import com.gb.trjamich.project.cloudstorage.utils.HandlerUtils;
import common.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FilesHandler extends ChannelInboundHandlerAdapter {
    public static final ConcurrentLinkedDeque<SocketChannel> channels = new ConcurrentLinkedDeque<>();
    private final HandlerUtils utils = new HandlerUtils();
    public Path currentServerPath = Path.of("server/serverDir");
    public static Path serverRoot = Path.of("server/serverDir");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("enter nav h");

        Request request = (Request) msg;

        if (request.getReqType().equals("file")) {
            if (request.getOperation().equals("upload")) {

            }
        }
    }
}
