package cn.cyejing.lastjump.intel.proto.handler;

import cn.cyejing.lastjump.intel.proto.model.ConnectResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Born
 */
public class ConnectResponseEncoder extends MessageToByteEncoder<ConnectResponse> {

    protected void encode(ChannelHandlerContext ctx, ConnectResponse msg, ByteBuf out) throws Exception {
        out.writeByte(msg.getVersion().byteValue());
        out.writeByte(msg.getType().byteValue());
        out.writeByte(0x00);

    }

}
