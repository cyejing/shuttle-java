package cn.cyejing.shuttle.center;

import cn.cyejing.shuttle.common.BootArgs;
import cn.cyejing.shuttle.common.CenterArgs;
import cn.cyejing.shuttle.common.encryption.CryptoFactory;
import cn.cyejing.shuttle.common.handler.ConnectRequestDecoder;
import cn.cyejing.shuttle.common.handler.ConnectResponseEncoder;
import cn.cyejing.shuttle.common.handler.CryptoCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Born
 */
@Slf4j
public class CenterBootstrap {
    public static final BootArgs config = new CenterArgs();

    public static void main(String[] args) throws Exception {
        config.initArgs(args);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new ServerBootstrap().group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new LengthFieldPrepender(4),
                                    new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,
                                            4, 0, 4),
                                    new CryptoCodec(config.getCryptoName(), config.getCryptoPassword()),
                                    new LoggingHandler(LogLevel.DEBUG),
                                    new ConnectRequestDecoder(),
                                    new ConnectResponseEncoder(),
                                    new CenterConnectHandler());

                        }
                    })
                    .bind(config.getPort()).sync();
            log.info("shuttle center started at {}", channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }


}
