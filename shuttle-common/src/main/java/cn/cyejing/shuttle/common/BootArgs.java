package cn.cyejing.shuttle.common;

import cn.cyejing.shuttle.common.encryption.CryptoFactory;
import io.netty.util.internal.StringUtil;
import java.util.Map;
import java.util.Properties;
import lombok.Data;

/**
 * @author Born
 */
@Data
public abstract class BootArgs {
    protected int port = -1;
    protected String auth;
    protected String remoteHost;
    protected int remotePort = 14845;
    protected String cryptoName="aes-128-cfb";
    protected String cryptoPassword="123456";


    public void initArgs(String[] args) {
        String port = getSystemArgs("port");
        if (!StringUtil.isNullOrEmpty(port)) {
            setPort(Integer.parseInt(port));
        }
        String remoteHost = getSystemArgs("remoteHost");
        if (!StringUtil.isNullOrEmpty(remoteHost)) {
            this.remoteHost = remoteHost;
        }
        String remotePort = getSystemArgs("remotePort");
        if (!StringUtil.isNullOrEmpty(remotePort)) {
            this.remotePort = Integer.parseInt(remotePort);
        }
        String cryptoName = getSystemArgs("cryptoName");
        if (!StringUtil.isNullOrEmpty(cryptoName)) {
            this.cryptoName = cryptoName;
        }
        String cryptoPassword = getSystemArgs("cryptoPassword");
        if (!StringUtil.isNullOrEmpty(cryptoPassword)) {
            this.cryptoPassword = cryptoPassword;
        }

        for (String arg : args) {
            if (arg.startsWith("--port")) {
                setPort(Integer.parseInt(arg.substring("--port".length() + 1)));
            }
            if (arg.startsWith("--auth")) {
                this.auth = arg.substring("--auth".length() + 1);
            }
            if (arg.startsWith("--remoteHost")) {
                this.remoteHost = arg.substring("--remoteHost".length() + 1);
            }
            if (arg.startsWith("--remotePort")) {
                this.remotePort = Integer.parseInt(arg.substring("--remotePort".length() + 1));
            }
            if (arg.startsWith("--cryptoName")) {
                this.cryptoName = arg.substring("--cryptoName".length() + 1);
            }
            if (arg.startsWith("--cryptoPassword")) {
                this.cryptoPassword = arg.substring("--cryptoPassword".length() + 1);
            }
        }


        verify();
    }

    private String getSystemArgs(String key) {
        String val = System.getProperty(key);
        if (StringUtil.isNullOrEmpty(val)) {
            val = System.getenv("SHUTTLE_" + key);
        }
        return val;
    }

    protected boolean verify(){
        if (getPort() < 1024) {
            throw new IllegalArgumentException(
                    "Illegal port:" + getPort());
        }
        if (!CryptoFactory.legalName(this.cryptoName)) {
            throw new IllegalArgumentException(
                    "unsupported crypto name:" + this.cryptoName + ". now support name is:" + CryptoFactory.supportName());
        }
        return true;
    }

}
