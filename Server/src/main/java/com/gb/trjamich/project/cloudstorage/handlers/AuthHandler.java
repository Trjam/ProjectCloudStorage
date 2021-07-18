package com.gb.trjamich.project.cloudstorage.handlers;

import com.gb.trjamich.project.cloudstorage.classes.HandlerUtils;
import com.gb.trjamich.project.cloudstorage.classes.Request;
import com.gb.trjamich.project.cloudstorage.classes.Response;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.ConcurrentLinkedDeque;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    public static final ConcurrentLinkedDeque<SocketChannel> channels = new ConcurrentLinkedDeque<>();
    public static ChannelHandlerContext ctx;
    private final HandlerUtils utils = new HandlerUtils();

    @Override
    public void channelActive(ChannelHandlerContext chc) {
        System.out.println("client connected: " + chc.channel());
        channels.add((SocketChannel) chc.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext chc, Object msg) {
        ctx = chc;
        Request request = utils.getRequest(msg);

        if (request.getReqType().equals("auth")) {
            switch (request.getOperation()) {
                case "login":
                    if (SQLHandler.checkPassword(
                            request.getUser().getLogin(),
                            request.getUser().getPassword()))
                    {
                        sendResponse(utils.authOkResponse(request));
                    } else {
                        sendResponse(utils.authFaultResponse("Wrong login or password", request));
                    }
                    break;
                case "register":
                    if (SQLHandler.registration(
                            request.getUser().getLogin(),
                            request.getUser().getPassword(),
                            request.getUser().getNickname()))
                    {
                        sendResponse(utils.authOkResponse(request));
                    } else {
                        sendResponse(utils.authFaultResponse("Login or nickname already exist", request));
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
    public void channelReadComplete(ChannelHandlerContext chc) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext chc, Throwable cause) {
        cause.printStackTrace();
        chc.close();
    }

    public void sendResponse(Response response) {
        Gson g = new Gson();
        String msg = g.toJson(response, Response.class);
        ctx.writeAndFlush(msg);
    }

}
