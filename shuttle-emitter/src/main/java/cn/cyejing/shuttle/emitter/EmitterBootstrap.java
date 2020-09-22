package cn.cyejing.shuttle.emitter;

import cn.cyejing.shuttle.common.BootArgs;
import cn.cyejing.shuttle.common.EmitterArgs;
import cn.cyejing.shuttle.common.encryption.CryptoFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Born
 */
@Slf4j
public class EmitterBootstrap {

    public static final BootArgs config = new EmitterArgs();

    public static void main(String[] args) throws Exception {
        config.initArgs(args);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new SocksPortUnificationServerHandler(),
                                    SocksServerHandler.INSTANCE);
                        }
                    })
                    .bind(config.getPort()).sync();
            log.info("shuttle emitter started at {}", channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}
