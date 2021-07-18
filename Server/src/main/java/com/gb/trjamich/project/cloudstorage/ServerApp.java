package com.gb.trjamich.project.cloudstorage;

import com.gb.trjamich.project.cloudstorage.Handlers.SQLHandler;
import com.gb.trjamich.project.cloudstorage.Handlers.UserHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
                                    //new ObjectDecoder(ClassResolver.cacheDisabled(null)),
                                    //new ObjectEncoder(),
                                    //new JsonObjectDecoder(),
                                    //new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()),
                                    new LineBasedFrameDecoder(200,true,true),
                                    new StringDecoder(CharsetUtil.UTF_8),
                                    new StringEncoder(CharsetUtil.UTF_8),
                                    new UserHandler()
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

    public static void main(String[] args) {
        new ServerApp();

    }
}


