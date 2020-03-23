package cn.cyejing.lastjump.spy;

import cn.cyejing.lastjump.intel.proto.handler.ConnectRequestEncoder;
import cn.cyejing.lastjump.intel.proto.handler.ConnectResponseDecoder;
import cn.cyejing.lastjump.intel.proto.model.ConnectRequest.ConnectType;
import cn.cyejing.lastjump.intel.proto.model.ConnectResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

public final class CIAConnectedHandler extends SimpleChannelInboundHandler<ConnectResponse> {

    private final Promise<Channel> promise;

    public CIAConnectedHandler(Promise<Channel> promise) {
        this.promise = promise;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        promise.setFailure(throwable);
    }

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
