
package cn.cyejing.shuttle.common.handler;


import cn.cyejing.shuttle.common.model.ConnectRequest.ConnectAddressType;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;

public interface Socks5AddressEncoder {

    Socks5AddressEncoder DEFAULT = (addrType, addrValue, out) -> {
        final byte typeVal = addrType.byteValue();
        if (typeVal == ConnectAddressType.IPv4.byteValue()) {
            if (addrValue != null) {
                out.writeBytes(NetUtil.createByteArrayFromIpAddressString(addrValue));
            } else {
                out.writeInt(0);
            }
        } else if (typeVal == ConnectAddressType.DOMAIN.byteValue()) {
            if (addrValue != null) {
                out.writeByte(addrValue.length());
                out.writeCharSequence(addrValue, CharsetUtil.US_ASCII);
            } else {
                out.writeByte(0);
            }
        } else if (typeVal == ConnectAddressType.IPv6.byteValue()) {
            if (addrValue != null) {
                out.writeBytes(NetUtil.createByteArrayFromIpAddressString(addrValue));
            } else {
                out.writeLong(0);
                out.writeLong(0);
            }
        } else {
            throw new EncoderException("unsupported addrType: " + (addrType.byteValue() & 0xFF));
        }
    };


    void encodeAddress(ConnectAddressType addrType, String addrValue, ByteBuf out);
}
