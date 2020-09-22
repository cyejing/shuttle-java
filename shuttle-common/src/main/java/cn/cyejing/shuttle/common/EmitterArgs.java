package cn.cyejing.shuttle.common;

import io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Born
 */
public class EmitterArgs extends BootArgs {

    @Getter
    @Setter
     protected int port = 14843;

    @Override
    protected void verify0() {
        if (StringUtil.isNullOrEmpty(this.remoteHost)) {
            throw new IllegalArgumentException("remoteHost must config, for args \"--remoteHost=x.x.x.x\"");
        }
    }
}
