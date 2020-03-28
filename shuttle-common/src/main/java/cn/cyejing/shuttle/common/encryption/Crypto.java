package cn.cyejing.shuttle.common.encryption;

public interface Crypto {

    byte[] encrypt(byte[] data);


    byte[] decrypt(byte[] data);


}
