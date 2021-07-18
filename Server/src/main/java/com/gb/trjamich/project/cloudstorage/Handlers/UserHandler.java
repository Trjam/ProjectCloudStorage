package com.gb.trjamich.project.cloudstorage.Handlers;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedDeque;

public class UserHandler extends ChannelInboundHandlerAdapter {
    public static final ConcurrentLinkedDeque<SocketChannel> channels = new ConcurrentLinkedDeque<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("client connected: " + ctx.channel());
        channels.add((SocketChannel) ctx.channel());
        System.out.println("Channel Active");
        try {
            ctx.write(Unpooled.copiedBuffer("Hello Client\r\n".getBytes(StandardCharsets.UTF_8)));
            ctx.flush();
            ctx.fireChannelReadComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String str = (String) msg;
        str = str.trim()+"\n";

        System.out.println("server received: " + str);
        ctx.write(str);
        ctx.flush();
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
