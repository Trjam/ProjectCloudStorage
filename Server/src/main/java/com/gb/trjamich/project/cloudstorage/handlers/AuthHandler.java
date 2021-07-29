package com.gb.trjamich.project.cloudstorage.handlers;

import com.gb.trjamich.project.cloudstorage.utils.HandlerUtils;
import common.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    public static final ConcurrentLinkedDeque<SocketChannel> channels = new ConcurrentLinkedDeque<>();
    private final HandlerUtils utils = new HandlerUtils();
    private static Map<String,UUID> users = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("client connected: " + ctx.channel());
        channels.add((SocketChannel) ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("disconnect");
        //ctx.close();
        System.out.println(ctx.channel().isActive());;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);

        Request request = utils.getRequest(msg);
        //System.out.println(msg);

        if (request.getReqType().equals("auth")) {
            if ("login".equals(request.getOperation())) {
                System.out.println("login start");
                if (SQLHandler.checkPassword(
                        request.getUser().getPassword(),
                        request.getUser().getLogin())) {
                    UUID uuid = UUID.randomUUID();
                    users.put(request.getUser().getLogin(), uuid);
                    utils.sendResponse(ctx, utils.authLoginOkResponse(request, uuid));


                } else {
                    System.out.println("login fault");
                    utils.sendResponse(ctx, utils.faultResponse("Wrong login or password", request));
                }
            } else if ("register".equals(request.getOperation())) {
                if (SQLHandler.registration(
                        request.getUser().getLogin(),
                        request.getUser().getPassword(),
                        request.getUser().getNickname())) {
                    utils.sendResponse(ctx, utils.authOkResponse(request));

                } else {
                    utils.sendResponse(ctx, utils.faultResponse("Login or nickname already exist", request));
                }
            } else if ("logout".equals(request.getOperation())) {
                users.remove(request.getUser().getLogin());
                ctx.close();
            }
        } else if (request.getToken().equals(users.get(request.getUser().getLogin()))){
            System.out.println("go to nav h");
            ctx.fireChannelRead(request);
        } else {
            System.out.println(users.get(request.getUser().getLogin()));
            utils.sendResponse(ctx,utils.faultResponse("Wrong token for user", request));
        }
        System.out.println("read end");
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
