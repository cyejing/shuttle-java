package cn.cyejing.shuttle.common;

import cn.cyejing.shuttle.common.encryption.CryptoFactory;
import io.netty.util.internal.StringUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Born
 */
@Data
public abstract class BootArgs {
    protected String auth;
    protected String remoteHost;
    protected int remotePort = 14845;
    protected String cryptoName="aes";
    protected String cryptoPassword="123456";

    protected Map<String, String> programArgs = new HashMap<>();

    public void initArgs(String[] args) {

        for (String arg : args) {
            if (arg.startsWith("--")) {
                programArgs.put(arg.substring(2, arg.lastIndexOf('=')), arg.substring(arg.lastIndexOf('=') + 1));
            }
        }

        String port = extractArgs("port");
        if (!StringUtil.isNullOrEmpty(port)) {
            setPort(Integer.parseInt(port));
        }
        String remoteHost = extractArgs("remoteHost");
        if (!StringUtil.isNullOrEmpty(remoteHost)) {
            this.remoteHost = remoteHost;
        }
        String remotePort = extractArgs("remotePort");
        if (!StringUtil.isNullOrEmpty(remotePort)) {
            this.remotePort = Integer.parseInt(remotePort);
        }
        String cryptoName = extractArgs("cryptoName");
        if (!StringUtil.isNullOrEmpty(cryptoName)) {
            this.cryptoName = cryptoName;
        }
        String cryptoPassword = extractArgs("cryptoPassword");
        if (!StringUtil.isNullOrEmpty(cryptoPassword)) {
            this.cryptoPassword = cryptoPassword;
        }
        String auth = extractArgs("auth");
        if (!StringUtil.isNullOrEmpty(auth)) {
            this.auth = auth;
        }

        verify();
    }


    private String extractArgs(String key) {
        String val = programArgs.get(key);
        if (StringUtil.isNullOrEmpty(val)) {
            val = System.getProperty(key);
        }
        if (StringUtil.isNullOrEmpty(val)) {
            val = System.getenv("SHUTTLE_" + key);
        }
        return val;
    }

    protected boolean verify() {
        if (getPort() < 1024) {
            throw new IllegalArgumentException(
                    "Illegal port:" + getPort());
        }
        if (!CryptoFactory.legalName(this.cryptoName)) {
            throw new IllegalArgumentException(
                    "unsupported crypto name:" + this.cryptoName + ". now support name is:" + CryptoFactory.supportName());
        }
        return verify0();
    }


    public abstract void setPort(int port);

    public abstract int getPort();

    protected abstract boolean verify0();
}
