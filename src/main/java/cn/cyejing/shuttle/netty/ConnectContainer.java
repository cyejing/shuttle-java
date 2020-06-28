/*
 * frxs Inc.  湖南兴盛优选电子商务有限公司.
 * Copyright (c) 2017-2019. All Rights Reserved.
 */
package cn.cyejing.shuttle.netty;

import cn.cyejing.shuttle.common.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.CharsetUtil;

import javax.net.ssl.SSLException;

/**
 * <B>主类名称：</B><BR>
 * <B>概要说明：</B><BR>
 *
 * @author Born
 * @since 2020年06月28日 3:55 下午
 */
public class ConnectContainer {

    private Config config;
    private Bootstrap bootstrap = new Bootstrap();
    private SslContext sslContext;

    public ConnectContainer(Config config) throws SSLException {
        this.config = config;
        NioEventLoopGroup work = new NioEventLoopGroup();

        sslContext = SslContextBuilder.forClient().build();

        bootstrap.group(work)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(sslContext.newHandler(ch.alloc()),
                                new HttpClientCodec(),
                                new SimpleChannelInboundHandler<HttpObject>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                                        if (msg instanceof HttpResponse) {
                                            HttpResponse response = (HttpResponse) msg;

                                            System.err.println("STATUS: " + response.status());
                                            System.err.println("VERSION: " + response.protocolVersion());
                                            System.err.println();

                                            if (!response.headers().isEmpty()) {
                                                for (CharSequence name: response.headers().names()) {
                                                    for (CharSequence value: response.headers().getAll(name)) {
                                                        System.err.println("HEADER: " + name + " = " + value);
                                                    }
                                                }
                                                System.err.println();
                                            }

                                            if (HttpUtil.isTransferEncodingChunked(response)) {
                                                System.err.println("CHUNKED CONTENT {");
                                            } else {
                                                System.err.println("CONTENT {");
                                            }
                                        }
                                        if (msg instanceof HttpContent) {
                                            HttpContent content = (HttpContent) msg;

                                            System.err.print(content.content().toString(CharsetUtil.UTF_8));
                                            System.err.flush();

                                            if (content instanceof LastHttpContent) {
                                                System.err.println("} END OF CONTENT");
                                                ctx.close();
                                            }
                                        }
                                    }
                                });
                    }
                });
    }

    public void request(String host,int port,HttpRequest httpRequest) throws InterruptedException {
        bootstrap.connect(host, port).sync().channel()
                .writeAndFlush(httpRequest);
    }
}
