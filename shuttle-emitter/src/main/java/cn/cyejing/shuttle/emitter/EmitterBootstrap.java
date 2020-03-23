package cn.cyejing.shuttle.emitter;

import cn.cyejing.shuttle.common.BootArgs;
import cn.cyejing.shuttle.common.EmitterArgs;
import cn.cyejing.shuttle.common.encryption.CryptoFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.StringUtil;

/**
 * @author Born
 */
public class EmitterBootstrap {

    public static final BootArgs config = new EmitterArgs();

    public static void main(String[] args) throws Exception {
        config.initArgs(args);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new SocksPortUnificationServerHandler(),
                                    SocksServerHandler.INSTANCE);
                        }
                    })
                    .bind(config.getPort()).sync()
                    .channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}
