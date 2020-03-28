package cn.cyejing.shuttle.common.encryption.impl;

import cn.cyejing.shuttle.common.encryption.CryptoBase;
import cn.cyejing.shuttle.common.encryption.exception.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

public class AesCrypto extends CryptoBase {

    private static final String AES = "AES";

    public static Map<String, String> getCiphers() {
        Map<String, String> ciphers = new HashMap<>();
        ciphers.put("aes", AesCrypto.class.getName());
        return ciphers;
    }

    private byte[] passwordBytes = new byte[16];

    public AesCrypto(String name, String password) {
        super(name, password);
        int length = password.getBytes().length;
        System.arraycopy(passwordBytes, 0, password.getBytes(), 0, length > 16 ? 16 : length);
    }

    @Override
    public byte[] encrypt(byte[] data) {
        try {
            SecretKey secretKey = new SecretKeySpec(passwordBytes, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    @Override
    public byte[] decrypt(byte[] data) {
        try {
            SecretKey secretKey = new SecretKeySpec(passwordBytes, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }


}
