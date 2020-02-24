package cn.cyejing.lastjump.spy;

import cn.cyejing.lastjump.intel.proto.encryption.CryptoFactory;
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
public class SpyBootstrap {

    public static final BootArgs config = new BootArgs();

    public static void main(String[] args) throws Exception {
       extractArgs(args);

        if (StringUtil.isNullOrEmpty(config.remoteHost)) {
            throw new IllegalArgumentException("remoteHost must config, for args \"--remoteHost=x.x.x.x\"");
        }
        if (!CryptoFactory.legalName(config.cryptoName)) {
            throw new IllegalArgumentException(
                    "unsupported crypto name:" + config.cryptoName + ". now support name is:" + CryptoFactory.supportName());
        }

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
                    .bind(config.port).sync()
                    .channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static void extractArgs(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--port")) {
                config.port = Integer.parseInt(arg.substring("--port".length() + 1));
            }
            if (arg.startsWith("--auth")) {
                config.auth = arg.substring("--auth".length() + 1);
            }
            if (arg.startsWith("--remoteHost")) {
                config.remoteHost = arg.substring("--remoteHost".length() + 1);
            }
            if (arg.startsWith("--remotePort")) {
                config.remotePort = Integer.parseInt(arg.substring("--remotePort".length() + 1));
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
        public int port = 14843;
        public String auth;
        public String remoteHost;
        public int remotePort = 14845;
        public String cryptoName="aes-128-cfb";
        public String cryptoPassword="123456";

    }
}
