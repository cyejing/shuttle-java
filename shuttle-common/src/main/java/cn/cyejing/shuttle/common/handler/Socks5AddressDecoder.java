
package cn.cyejing.shuttle.common.handler;


import cn.cyejing.shuttle.common.model.ConnectRequest.ConnectAddressType;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;

public interface Socks5AddressDecoder {

    Socks5AddressDecoder DEFAULT = new Socks5AddressDecoder() {

        private static final int IPv6_LEN = 16;

        public String decodeAddress(ConnectAddressType addrType, ByteBuf in) {
            if (addrType == ConnectAddressType.IPv4) {
                return NetUtil.intToIpAddress(in.readInt());
            }
            if (addrType == ConnectAddressType.DOMAIN) {
                final int length = in.readUnsignedByte();
                final String domain = in.toString(in.readerIndex(), length, CharsetUtil.US_ASCII);
                in.skipBytes(length);
                return domain;
            }
            if (addrType == ConnectAddressType.IPv6) {
                if (in.hasArray()) {
                    final int readerIdx = in.readerIndex();
                    in.readerIndex(readerIdx + IPv6_LEN);
                    return NetUtil.bytesToIpAddress(in.array(), in.arrayOffset() + readerIdx, IPv6_LEN);
                } else {
                    byte[] tmp = new byte[IPv6_LEN];
                    in.readBytes(tmp);
                    return NetUtil.bytesToIpAddress(tmp);
                }
            } else {
                throw new DecoderException("unsupported address type: " + (addrType.byteValue() & 0xFF));
            }
        }
    };

    String decodeAddress(ConnectAddressType addrType, ByteBuf in);
}
