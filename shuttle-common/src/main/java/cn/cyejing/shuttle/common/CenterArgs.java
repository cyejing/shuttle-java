package cn.cyejing.shuttle.common;


import lombok.Getter;
import lombok.Setter;

/**
 * @author Born
 */
public class CenterArgs extends BootArgs {

    @Getter
    @Setter
    protected int port = 14845;

    @Override
    protected boolean verify0() {
        return true;
    }


}
