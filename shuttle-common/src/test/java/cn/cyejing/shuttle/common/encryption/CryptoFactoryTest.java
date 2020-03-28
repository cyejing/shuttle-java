package cn.cyejing.shuttle.common.encryption;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author Born
 * @version : CryptoFactoryTest.java,v 0.1 2020年03月28日 15:07
 */
public class CryptoFactoryTest {

    @Test
    public void testCrypto() {
        Crypto crypto = CryptoFactory.get("aes", "123456");
        String content = "123qweasd";
        System.out.println(Arrays.toString(content.getBytes()));
        byte[] encrypt = crypto.encrypt(content.getBytes());
        System.out.println(Arrays.toString(encrypt));
        byte[] decrypt = crypto.decrypt(encrypt);
        System.out.println(Arrays.toString(decrypt));
        System.out.println(new String(decrypt));

    }

}
