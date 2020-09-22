package cn.cyejing.shuttle.emitter;

import cn.cyejing.shuttle.common.handler.ConnectRequestEncoder;
import cn.cyejing.shuttle.common.handler.ConnectResponseDecoder;
import cn.cyejing.shuttle.common.model.ConnectRequest.ConnectType;
import cn.cyejing.shuttle.common.model.ConnectResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

/**
 * @author cyejing
 */
public final class CenterConnectedHandler extends SimpleChannelInboundHandler<ConnectResponse> {

    private final Promise<Channel> promise;

    public CenterConnectedHandler(Promise<Channel> promise) {
        this.promise = promise;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        promise.setFailure(throwable);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnectResponse msg) {
        if (ConnectType.Connected.equals(msg.getType())) {
            ctx.pipeline().remove(ConnectRequestEncoder.class);
            ctx.pipeline().remove(ConnectResponseDecoder.class);
            ctx.pipeline().remove(this);
            promise.setSuccess(ctx.channel());
        } else {
            ctx.close();
        }
    }
}
