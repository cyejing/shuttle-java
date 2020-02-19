package cn.cyejing.lastjump.intel.proto.handler;

import cn.cyejing.lastjump.intel.proto.model.ConnectRequest;
import cn.cyejing.lastjump.intel.proto.model.ConnectRequest.ConnectAddressType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Born
 */
public class ConnectRequestEncoder extends MessageToByteEncoder<ConnectRequest> {
    private Socks5AddressEncoder addressEncoder = Socks5AddressEncoder.DEFAULT;

    protected void encode(ChannelHandlerContext ctx, ConnectRequest msg, ByteBuf out) throws Exception {
        out.writeByte(msg.getVersion().byteValue());
        out.writeByte(msg.getType().byteValue());
        out.writeByte(0x00);
        final ConnectAddressType type = msg.getAddressType();
        out.writeByte(type.byteValue());
        addressEncoder.encodeAddress(type, msg.getRemoteHost(), out);
        out.writeShort(msg.getRemotePort());
    }
}
