/*
 * frxs Inc.  湖南兴盛优选电子商务有限公司.
 * Copyright (c) 2017-2019. All Rights Reserved.
 */
package cn.cyejing.shuttle.common;

import lombok.Data;

/**
 * <B>主类名称：</B><BR>
 * <B>概要说明：</B><BR>
 *
 * @author Born
 * @since 2020年06月25日 1:25 下午
 */
@Data
public class Config {
    private int port = 14843;

    private int maxContentLength = 32 * 1024 * 1024;

    private String certificateFile = "test.pem";
    private String keyFile = "test.key";

}
