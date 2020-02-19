package cn.cyejing.lastjump.intel.proto.encryption;

import cn.cyejing.lastjump.intel.proto.encryption.impl.AesCrypto;
import cn.cyejing.lastjump.intel.proto.encryption.impl.BlowFishCrypto;
import cn.cyejing.lastjump.intel.proto.encryption.impl.CamelliaCrypto;
import cn.cyejing.lastjump.intel.proto.encryption.impl.SeedCrypto;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoFactory {

    private static Logger logger = LoggerFactory.getLogger(CryptoFactory.class);

    private static Map<String, String> crypts = new HashMap<>();
    private static Map<String, Crypto> instanceMap = new HashMap<>();

    static {
        crypts.putAll(AesCrypto.getCiphers());
        crypts.putAll(CamelliaCrypto.getCiphers());
        crypts.putAll(BlowFishCrypto.getCiphers());
        crypts.putAll(SeedCrypto.getCiphers());
    }

    public static Crypto get(String name, String password) {
//        if (instanceMap.containsKey(name + ":" + password)) {
//            return instanceMap.get(name + ":" + password);
//        }

        String className = crypts.get(name);
        if (className == null) {
            return null;
        }

        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(String.class,
                    String.class);
            Crypto crypto = (Crypto) constructor.newInstance(name, password);
//            instanceMap.put(name + ":" + password, crypto);
            return crypto;
        } catch (Exception e) {
            logger.error("get crypt error", e);
        }

        return null;
    }
}
