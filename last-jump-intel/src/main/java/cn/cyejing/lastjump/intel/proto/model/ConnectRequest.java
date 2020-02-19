package cn.cyejing.lastjump.intel.proto.model;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import lombok.Data;

/**
 * @author Born
 */
@Data
public class ConnectRequest {

    private Version version = Version.V1;
    private ConnectType type;
    private ConnectAddressType addressType;
    private String remoteHost;
    private int remotePort;

    public ConnectRequest() {
    }

    public ConnectRequest(ConnectType type,ConnectAddressType addressType, String remoteHost, int remotePort) {
        this.type = type;
        this.addressType = addressType;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    public enum Version {
        V1((byte) 0x01);

        private final byte b;


        Version(byte b) {
            this.b = b;
        }

        public byte byteValue() {
            return b;
        }

    }

    public enum ConnectType{
        Failed((byte) 0x00),
        Connect((byte) 0x01),
        Connected((byte) 0x02),
        ;

        private final byte byteValue;

        ConnectType(byte byteValue) {
            this.byteValue = byteValue;
        }

        public static ConnectType valueOf(byte b) {
            switch (b) {
                case 0x00:
                    return Failed;
                case 0x01:
                    return Connect;
                case 0x02:
                    return Connected;

                default:
                    throw new DecoderException("unsupported host type");
            }
        }

        public byte byteValue() {
            return byteValue;
        }

    }

    public enum ConnectAddressType {
        IPv4((byte) 0x01, "IPv4"),
        DOMAIN((byte) 0x03, "DOMAIN"),
        IPv6((byte) 0x04, "IPv6"),
        ;

        private final byte byteValue;
        private final String name;

        ConnectAddressType(byte byteValue, String name) {
            this.byteValue = byteValue;
            this.name = name;
        }

        public static ConnectAddressType valueOf(Socks5AddressType dstAddrType) {
            switch (dstAddrType.byteValue()) {
                case 0x01: return ConnectAddressType.IPv4;
                case 0x03: return ConnectAddressType.DOMAIN;
                case 0x04: return ConnectAddressType.IPv6;
                default:
                    throw new DecoderException("unsupported host type");
            }
        }

        public byte byteValue() {
            return byteValue;
        }


        public static ConnectAddressType valueOf(byte b) {
            switch (b) {
                case 0x01:
                    return IPv4;
                case 0x03:
                    return DOMAIN;
                case 0x04:
                    return IPv6;
                default:
                    throw new DecoderException("unsupported host type");
            }

        }

    }
}
