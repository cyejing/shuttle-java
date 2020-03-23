package cn.cyejing.lastjump.cia;

import cn.cyejing.lastjump.intel.proto.encryption.CryptoFactory;
import cn.cyejing.lastjump.intel.proto.handler.ConnectRequestDecoder;
import cn.cyejing.lastjump.intel.proto.handler.ConnectResponseEncoder;
import cn.cyejing.lastjump.intel.proto.handler.CryptoCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Born
 */
public class CIABootstrap {
    public static final BootArgs config = new BootArgs();

    public static void main(String[] args) throws Exception {
        extractArgs(args);

        if (!CryptoFactory.legalName(config.cryptoName)) {
            throw new IllegalArgumentException(
                    "unsupported crypto name:" + config.cryptoName + ". now support name is:" + CryptoFactory.supportName());
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            new ServerBootstrap().group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new LengthFieldPrepender(4),
                                    new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,
                                            4, 0, 4),
                                    new CryptoCodec(config.cryptoName, config.cryptoPassword),
                                    new LoggingHandler(LogLevel.DEBUG),
                                    new ConnectRequestDecoder(),
                                    new ConnectResponseEncoder(),
                                    new CIAConnectHandler());

                        }
                    })
                    .bind(config.port).sync()
                    .channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    private static void extractArgs(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--port")) {
                config.port = Integer.parseInt(arg.substring("--port".length() + 1));
            }
            if (arg.startsWith("--cryptoName")) {
                config.cryptoName = arg.substring("--cryptoName".length() + 1);
            }
            if (arg.startsWith("--cryptoPassword")) {
                config.cryptoPassword = arg.substring("--cryptoPassword".length() + 1);
            }
        }
    }

    public static class BootArgs {

        public int port = 14845;
        public String cryptoName="aes-128-cfb";
        public String cryptoPassword="123456";
    }
}
