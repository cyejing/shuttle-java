package cn.cyejing.shuttle.common.handler;

import cn.cyejing.shuttle.common.model.ConnectRequest.ConnectType;
import cn.cyejing.shuttle.common.model.ConnectRequest.Version;
import cn.cyejing.shuttle.common.model.ConnectResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;

/**
 * @author Born
 */
public class ConnectResponseDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte version = in.readByte();
        if (version != Version.V1.byteValue()) {
            throw new DecoderException(
                    "unsupported version: " + version + " (expected: " + Version.V1.byteValue() + ')');
        }
        ConnectType type = ConnectType.valueOf(in.readByte());
        in.skipBytes(1); // RSV
        out.add(new ConnectResponse(type));
    }
}
