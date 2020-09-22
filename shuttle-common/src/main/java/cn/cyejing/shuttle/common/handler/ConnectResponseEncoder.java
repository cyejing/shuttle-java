package cn.cyejing.shuttle.common.handler;

import cn.cyejing.shuttle.common.model.ConnectResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Born
 */
public class ConnectResponseEncoder extends MessageToByteEncoder<ConnectResponse> {

    protected void encode(ChannelHandlerContext ctx, ConnectResponse msg, ByteBuf out) {
        out.writeByte(msg.getVersion().byteValue());
        out.writeByte(msg.getType().byteValue());
        out.writeByte(0x00);

    }

}
