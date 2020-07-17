/*
 * frxs Inc.  湖南兴盛优选电子商务有限公司.
 * Copyright (c) 2017-2019. All Rights Reserved.
 */
package cn.cyejing.shuttle;

import cn.cyejing.shuttle.common.Config;
import cn.cyejing.shuttle.netty.ServerContainer;
import cn.cyejing.shuttle.netty.SocksContainer;

/**
 * <B>主类名称：</B><BR>
 * <B>概要说明：</B><BR>
 *
 * @author Born
 * @since 2020年06月22日 5:31 下午
 */
public class Bootstrap {

    public static void main(String[] args) throws Exception {
        Config config = new Config();
        ServerContainer shuttleContainer = new ServerContainer(config);
        shuttleContainer.bind(14843);
//        SocksContainer socksContainer = new SocksContainer(config);
//        socksContainer.bind(4843);

    }
}
