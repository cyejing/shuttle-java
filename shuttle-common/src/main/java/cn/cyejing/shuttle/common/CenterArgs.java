package cn.cyejing.shuttle.common;


import lombok.Data;

/**
 * @author Born
 */
@Data
public class CenterArgs extends BootArgs {

    protected int port = 14845;

    @Override
    protected boolean verify() {
        super.verify();
        return true;
    }


}
