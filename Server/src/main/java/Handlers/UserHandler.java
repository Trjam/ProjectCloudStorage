package Handlers;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;

public class UserHandler extends ChannelInboundHandlerAdapter {

    public static final ConcurrentLinkedDeque<SocketChannel> channels = new ConcurrentLinkedDeque<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client connected: " + ctx.channel());
        channels.add((SocketChannel) ctx.channel());
        sendMessage("connected\n\r", ctx);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client disconnected: " + ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = String.valueOf(msg).replace("\r\n", "");
        System.out.println(message);
        if(message.equalsIgnoreCase("user")) {
            sendMessage("user\n\r",ctx);
        }
    }

    private void sendMessage(String message, ChannelHandlerContext ctx) throws IOException {
        ctx.write(message);
        ctx.flush();
    }
}
