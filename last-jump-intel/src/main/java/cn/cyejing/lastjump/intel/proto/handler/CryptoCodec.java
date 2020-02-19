package cn.cyejing.lastjump.intel.proto.handler;

import cn.cyejing.lastjump.intel.proto.HexUtil;
import cn.cyejing.lastjump.intel.proto.encryption.Crypto;
import cn.cyejing.lastjump.intel.proto.encryption.CryptoFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * @author Born
 */
public class CryptoCodec extends ByteToMessageCodec<ByteBuf> {

    private final Crypto crypto;

    public CryptoCodec(String cryptoName, String password) {
        this.crypto = CryptoFactory.get(cryptoName, password);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        crypto.encrypt(bytes, outputStream);
        out.writeBytes(outputStream.toByteArray());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        crypto.decrypt(bytes, outputStream);
        ByteBuf outBuf = ctx.alloc().buffer();
        outBuf.writeBytes(outputStream.toByteArray());
        out.add(outBuf);
    }

}
