package cn.cyejing.shuttle.center;

import cn.cyejing.shuttle.common.handler.ConnectRequestDecoder;
import cn.cyejing.shuttle.common.handler.ConnectResponseEncoder;
import cn.cyejing.shuttle.common.handler.RelayHandler;
import cn.cyejing.shuttle.common.model.ConnectRequest;
import cn.cyejing.shuttle.common.model.ConnectRequest.ConnectType;
import cn.cyejing.shuttle.common.model.ConnectResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Born
 */
@Slf4j
public class CenterConnectHandler extends SimpleChannelInboundHandler<ConnectRequest> {

    protected void channelRead0(ChannelHandlerContext context, ConnectRequest request) {
        log.info("requested connection to {}:{}", request.getRemoteHost(), request.getRemotePort());
        final ChannelHandlerContext ctx = context;
        new Bootstrap().group(ctx.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new RelayHandler(ctx.channel()))
                .connect(request.getRemoteHost(), request.getRemotePort())
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        ctx.channel().writeAndFlush(new ConnectResponse(ConnectType.Connected)).addListener(writeDone -> {
                            ctx.pipeline().remove(CenterConnectHandler.this);
                            ctx.pipeline().remove(ConnectRequestDecoder.class);
                            ctx.pipeline().remove(ConnectResponseEncoder.class);
                            ctx.pipeline().addLast(new RelayHandler(future.channel()));
                        });

                    } else {
                        log.error("failed connect remote host {}:{}", request.getRemoteHost(), request.getRemotePort());
                        ctx.channel().writeAndFlush(new ConnectResponse(ConnectType.Failed));
                        ctx.close();
                    }
                });
    }
}
