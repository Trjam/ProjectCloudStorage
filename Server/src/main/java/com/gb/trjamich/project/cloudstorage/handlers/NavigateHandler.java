package com.gb.trjamich.project.cloudstorage.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.ConcurrentLinkedDeque;

public class NavigateHandler extends ChannelInboundHandlerAdapter {
    public static final ConcurrentLinkedDeque<SocketChannel> channels = new ConcurrentLinkedDeque<>();
    public static ChannelHandlerContext ctx;

    @Override
    public void channelRead(ChannelHandlerContext chc, Object msg) {
        
    }
}
