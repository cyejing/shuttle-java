/*
 * frxs Inc.  湖南兴盛优选电子商务有限公司.
 * Copyright (c) 2017-2019. All Rights Reserved.
 */
package cn.cyejing.shuttle.netty;

import cn.cyejing.shuttle.common.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.io.InputStream;

/**
 * <B>主类名称：</B><BR>
 * <B>概要说明：</B><BR>
 *
 * @author Born
 * @since 2020年06月25日 1:20 下午
 */
@Slf4j
public class ServerContainer {

    private final Config config;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workGroup;
    private final ServerBootstrap serverBootstrap;
    private SslContext sslContext;

    public ServerContainer(Config config) throws SSLException {
        this.config = config;
        this.bossGroup = new NioEventLoopGroup(1);
        this.workGroup = new NioEventLoopGroup();

        InputStream certificateFile = getClass().getClassLoader().getResourceAsStream(config.getCertificateFile());
        InputStream keyFile = getClass().getClassLoader().getResourceAsStream(config.getKeyFile());
        sslContext = SslContextBuilder.forServer(certificateFile, keyFile)
                .startTls(true)
                .clientAuth(ClientAuth.OPTIONAL)
                .build();

        this.serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)                //	sync + accept = backlog
                .option(ChannelOption.SO_REUSEADDR, true)              //	允许重复使用本地地址和端口
                .option(ChannelOption.SO_KEEPALIVE, false)             //	如果在两小时内没有数据的通信时, TCP会自动发送一个活动探测数据报文
                .childOption(ChannelOption.TCP_NODELAY, true)          //	该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
                .childOption(ChannelOption.SO_SNDBUF, 65535)           //	设置发送数据的缓冲区大小
                .childOption(ChannelOption.SO_RCVBUF, 65535)           //	设置接受数据的缓冲区大小
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(
                                new SslHandler(sslContext.newEngine(sc.alloc())),
                                new HttpServerCodec(),
                                new HttpObjectAggregator(config.getMaxContentLength()),
                                new HttpServerExpectContinueHandler(),
                                new HttpHandler()
                        );
                    }
                });

    }

    public void bind(int port) throws InterruptedException {
        serverBootstrap.bind(port).sync();

    }
}
