package cn.cyejing.shuttle.emitter;

import cn.cyejing.shuttle.common.utils.SocksServerUtils;
import cn.cyejing.shuttle.common.handler.ConnectRequestEncoder;
import cn.cyejing.shuttle.common.handler.ConnectResponseDecoder;
import cn.cyejing.shuttle.common.handler.CryptoCodec;
import cn.cyejing.shuttle.common.handler.RelayHandler;
import cn.cyejing.shuttle.common.model.ConnectRequest;
import cn.cyejing.shuttle.common.model.ConnectRequest.ConnectAddressType;
import cn.cyejing.shuttle.common.model.ConnectRequest.ConnectType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.socksx.SocksMessage;
import io.netty.handler.codec.socksx.v4.DefaultSocks4CommandResponse;
import io.netty.handler.codec.socksx.v4.Socks4CommandRequest;
import io.netty.handler.codec.socksx.v4.Socks4CommandStatus;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;

@ChannelHandler.Sharable
public final class SocksServerConnectHandler extends SimpleChannelInboundHandler<SocksMessage> {

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final SocksMessage message) {
        if (message instanceof Socks4CommandRequest) {
            socks4CommandExec(ctx, (Socks4CommandRequest) message);
        } else if (message instanceof Socks5CommandRequest) {
            socks5CommandExec(ctx, (Socks5CommandRequest) message);
        } else {
            ctx.close();
        }
    }

    private void socks5CommandExec(final ChannelHandlerContext ctx, Socks5CommandRequest message) {
        final Socks5CommandRequest request = message;
        final Promise<Channel> promise = ctx.executor().newPromise();
        promise.addListener((FutureListener<Channel>) future -> {
            final Channel outboundChannel = future.getNow();
            if (future.isSuccess()) {
                ChannelFuture responseFuture =
                        ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                                Socks5CommandStatus.SUCCESS,
                                request.dstAddrType(),
                                request.dstAddr(),
                                request.dstPort()));

                responseFuture.addListener((ChannelFutureListener) channelFuture -> {
                    ctx.pipeline().remove(SocksServerConnectHandler.this);
                    outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
                    ctx.pipeline().addLast(new RelayHandler(outboundChannel));
                });
            } else {
                ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                        Socks5CommandStatus.FAILURE, request.dstAddrType()));
                SocksServerUtils.closeOnFlush(ctx.channel());
            }
        });

        final Channel inboundChannel = ctx.channel();
        connectCIA(promise, inboundChannel)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        future.channel().writeAndFlush(new ConnectRequest(ConnectType.Connect,
                                ConnectAddressType.valueOf(request.dstAddrType()),
                                request.dstAddr(), request.dstPort()));
                    } else {
                        ctx.channel().writeAndFlush(
                                new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE,
                                        request.dstAddrType()));
                        SocksServerUtils.closeOnFlush(ctx.channel());
                    }
                });
    }

    private void socks4CommandExec(final ChannelHandlerContext ctx, Socks4CommandRequest message) {
        final Socks4CommandRequest request = message;
        Promise<Channel> promise = ctx.executor().newPromise();
        promise.addListener((FutureListener<Channel>) future -> {
                    final Channel outboundChannel = future.getNow();
                    if (future.isSuccess()) {
                        ChannelFuture responseFuture = ctx.channel().writeAndFlush(
                                new DefaultSocks4CommandResponse(Socks4CommandStatus.SUCCESS));

                        responseFuture.addListener((ChannelFutureListener) channelFuture -> {
                            ctx.pipeline().remove(SocksServerConnectHandler.this);
                            ctx.pipeline().addLast(new RelayHandler(outboundChannel));
                            outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
                        });
                    } else {
                        ctx.channel().writeAndFlush(
                                new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED));
                        SocksServerUtils.closeOnFlush(ctx.channel());
                    }
                });

        final Channel inboundChannel = ctx.channel();
        connectCIA(promise, inboundChannel)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        future.channel().writeAndFlush(new ConnectRequest(ConnectType.Connect,
                                ConnectAddressType.IPv4, request.dstAddr(), request.dstPort()));
                    } else {
                        ctx.channel().writeAndFlush(
                                new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED)
                        );
                        SocksServerUtils.closeOnFlush(ctx.channel());
                    }
                });
    }

    private ChannelFuture connectCIA(Promise<Channel> promise, Channel inboundChannel) {
        return new Bootstrap().group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new LengthFieldPrepender(4),
                                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,
                                        4, 0, 4),
                                new CryptoCodec(SpyBootstrap.config.cryptoName, SpyBootstrap.config.cryptoPassword),
                                new LoggingHandler(LogLevel.DEBUG),
                                new ConnectRequestEncoder(),
                                new ConnectResponseDecoder(),
                                new CIAConnectedHandler(promise));
                    }
                })
                .connect(SpyBootstrap.config.remoteHost, SpyBootstrap.config.remotePort);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        SocksServerUtils.closeOnFlush(ctx.channel());
    }
}
