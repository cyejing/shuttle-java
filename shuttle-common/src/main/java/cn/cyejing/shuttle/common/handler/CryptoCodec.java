package cn.cyejing.shuttle.common.handler;

import cn.cyejing.shuttle.common.encryption.Crypto;
import cn.cyejing.shuttle.common.encryption.CryptoFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * @author Born
 */
@Slf4j
public class CryptoCodec extends ByteToMessageCodec<ByteBuf> {

    private final Crypto crypto;

    public CryptoCodec(String cryptoName, String password) {
        this.crypto = CryptoFactory.get(cryptoName, password);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out)  {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        out.writeBytes(crypto.encrypt(bytes));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)  {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        ByteBuf outBuf = ctx.alloc().buffer();
        outBuf.writeBytes(crypto.decrypt(bytes));
        out.add(outBuf);
    }

}
