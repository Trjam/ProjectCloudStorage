package Handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import java.io.IOException;

public class CommandHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("command connected");
        UserHandler.channels.add((SocketChannel) ctx.channel());
        //sendMessage("connected\n\r", ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = String.valueOf(msg);
        if(message.equalsIgnoreCase("command")) {
            sendMessage("command\n\r",ctx);
        }
    }

    private void sendMessage(String message, ChannelHandlerContext ctx) throws IOException {
        ctx.write(message);
        ctx.flush();
    }

}
