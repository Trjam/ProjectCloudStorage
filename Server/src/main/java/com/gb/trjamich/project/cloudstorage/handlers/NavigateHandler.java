package com.gb.trjamich.project.cloudstorage.handlers;

import com.gb.trjamich.project.cloudstorage.utils.HandlerUtils;
import com.gb.trjamich.project.cloudstorage.classes.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.ConcurrentLinkedDeque;

public class NavigateHandler extends ChannelInboundHandlerAdapter {
    public static final ConcurrentLinkedDeque<SocketChannel> channels = new ConcurrentLinkedDeque<>();
    private final HandlerUtils utils = new HandlerUtils();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        Request request = (Request) msg;

        if (request.getReqType().equals("nav")) {
            if (request.getOperation().equals("filelist")) {

            } else if (request.getOperation().equals("cd")){

            } else if (request.getOperation().equals("view")) {

            }

        } else {
            ctx.fireChannelRead(request);
        }
    }

    public void getFileList() {

    }

    public void changeDir () {

    }


}
