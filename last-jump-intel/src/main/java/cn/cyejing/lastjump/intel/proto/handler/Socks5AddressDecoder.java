/*
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package cn.cyejing.lastjump.intel.proto.handler;


import cn.cyejing.lastjump.intel.proto.model.ConnectRequest.ConnectAddressType;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;

/**
 * Decodes a SOCKS5 address field into its string representation.
 *
 */
public interface Socks5AddressDecoder {

    Socks5AddressDecoder DEFAULT = new Socks5AddressDecoder() {

        private static final int IPv6_LEN = 16;

        public String decodeAddress(ConnectAddressType addrType, ByteBuf in) throws Exception {
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

    /**
     * Decodes a SOCKS5 address field into its string representation.
     *
     * @param addrType the type of the address
     * @param in the input buffer which contains the SOCKS5 address field at its reader index
     */
    String decodeAddress(ConnectAddressType addrType, ByteBuf in) throws Exception;
}
