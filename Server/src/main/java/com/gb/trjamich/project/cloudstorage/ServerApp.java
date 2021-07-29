package com.gb.trjamich.project.cloudstorage;

import com.gb.trjamich.project.cloudstorage.handlers.FilesHandler;
import com.gb.trjamich.project.cloudstorage.handlers.NavigateHandler;
import com.gb.trjamich.project.cloudstorage.handlers.SQLHandler;
import com.gb.trjamich.project.cloudstorage.handlers.AuthHandler;
import common.Response;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ServerApp {

    public ServerApp() {

        EventLoopGroup auth = new NioEventLoopGroup(1); // light
        EventLoopGroup worker = new NioEventLoopGroup();


        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            SQLHandler.connect();
                            channel.pipeline().addLast(
                                    //new LineBasedFrameDecoder(8192),
                                    new StringDecoder(CharsetUtil.UTF_8),
                                    //new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()),
                                    new StringEncoder(CharsetUtil.UTF_8),
                                    new AuthHandler(),
                                    new NavigateHandler(),
                                    new FilesHandler()
                                    //new DownloadHandler()
                            );
                        }
                    });
            ChannelFuture future = bootstrap
                    .bind(5000)
                    .sync();

            System.out.println("Server started");
            future.channel().closeFuture().sync();
            System.out.println("Server finished");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void sendResponse (Response response) {

    }

    public static void sendMessage (String message) {

    }

    public static void main(String[] args) {
        new ServerApp();

    }
}


