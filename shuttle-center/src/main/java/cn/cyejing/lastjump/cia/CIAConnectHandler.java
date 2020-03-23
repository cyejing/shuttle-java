package cn.cyejing.lastjump.cia;

import cn.cyejing.lastjump.intel.proto.handler.ConnectRequestDecoder;
import cn.cyejing.lastjump.intel.proto.handler.ConnectResponseEncoder;
import cn.cyejing.lastjump.intel.proto.handler.RelayHandler;
import cn.cyejing.lastjump.intel.proto.model.ConnectRequest;
import cn.cyejing.lastjump.intel.proto.model.ConnectRequest.ConnectType;
import cn.cyejing.lastjump.intel.proto.model.ConnectResponse;
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
public class CIAConnectHandler extends SimpleChannelInboundHandler<ConnectRequest> {

    protected void channelRead0(ChannelHandlerContext context, ConnectRequest request) {
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
                            ctx.pipeline().remove(CIAConnectHandler.this);
                            ctx.pipeline().remove(ConnectRequestDecoder.class);
                            ctx.pipeline().remove(ConnectResponseEncoder.class);
                            ctx.pipeline().addLast(new RelayHandler(future.channel()));
                        });

                    } else {
                        log.error("connect remote host:{} fail", request.getRemoteHost());
                        ctx.channel().writeAndFlush(new ConnectResponse(ConnectType.Failed));
                        ctx.close();
                    }
                });
    }
}
