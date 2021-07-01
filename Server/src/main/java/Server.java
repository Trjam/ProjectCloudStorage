import Handlers.CommandHandler;
import Handlers.FilesHandler;
import Handlers.UserHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Server {

    public Server() {
        EventLoopGroup auth = new NioEventLoopGroup(1); // light
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new UserHandler(),
                                    new CommandHandler(),
                                    new FilesHandler()
                            );
                        }
                    });
            ChannelFuture future = bootstrap.bind(5000).sync();
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
        new Server();
    }
}


