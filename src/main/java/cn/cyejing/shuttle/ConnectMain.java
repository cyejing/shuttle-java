/*
 * frxs Inc.  湖南兴盛优选电子商务有限公司.
 * Copyright (c) 2017-2019. All Rights Reserved.
 */
package cn.cyejing.shuttle;

import cn.cyejing.shuttle.common.Config;
import cn.cyejing.shuttle.netty.ConnectContainer;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.net.URI;

/**
 * <B>主类名称：</B><BR>
 * <B>概要说明：</B><BR>
 *
 * @author Born
 * @since 2020年06月28日 4:22 下午
 */
public class ConnectMain {
    public static void main(String[] args) throws Exception {
        Config config = new Config();
        URI uri = new URI("http://127.0.0.1:14843/");
        ConnectContainer connectContainer = new ConnectContainer(config);
        HttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath(), Unpooled.EMPTY_BUFFER);
        request.headers().set(HttpHeaderNames.HOST, uri.getHost());
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

        connectContainer.request(uri.getHost(),uri.getPort(),request);
    }
}
