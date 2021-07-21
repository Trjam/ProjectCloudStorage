package com.gb.trjamich.project.cloudstorage.handlers;

import com.gb.trjamich.project.cloudstorage.utils.HandlerUtils;
import common.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.ConcurrentLinkedDeque;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    public static final ConcurrentLinkedDeque<SocketChannel> channels = new ConcurrentLinkedDeque<>();
    private final HandlerUtils utils = new HandlerUtils();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("client connected: " + ctx.channel());
        channels.add((SocketChannel) ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        Request request = utils.getRequest(msg);
        System.out.println(msg);

        if (request.getReqType().equals("auth")) {
            switch (request.getOperation()) {
                case "login":
                    System.out.println("login start");
                    if (SQLHandler.checkPassword(
                            request.getUser().getPassword(),
                            request.getUser().getLogin())) {
                        utils.sendResponse(ctx, utils.authOkResponse(request));
                    } else {
                        System.out.println("login fault");
                        utils.sendResponse(ctx, utils.authFaultResponse("Wrong login or password", request));
                    }
                    break;
                case "register":
                    if (SQLHandler.registration(
                            request.getUser().getLogin(),
                            request.getUser().getPassword(),
                            request.getUser().getNickname())) {
                        utils.sendResponse(ctx, utils.authOkResponse(request));
                    } else {
                        utils.sendResponse(ctx, utils.authFaultResponse("Login or nickname already exist", request));
                    }
                    break;
                case "logout":
                    ctx.close();
                    break;
            }

        } else {
            ctx.fireChannelRead(request);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
