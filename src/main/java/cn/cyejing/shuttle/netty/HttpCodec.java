/*
 * frxs Inc.  湖南兴盛优选电子商务有限公司.
 * Copyright (c) 2017-2019. All Rights Reserved.
 */
package cn.cyejing.shuttle.netty;

import cn.cyejing.shuttle.common.model.ShuttleProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 *
 *
 * @author Born
 * @since 2020年06月25日 5:32 下午
 */
@Slf4j
public class HttpCodec extends MessageToMessageCodec<FullHttpRequest, ShuttleProtocol> {


    @Override
    protected void encode(ChannelHandlerContext ctx, ShuttleProtocol msg, List<Object> out) throws Exception {

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception {
        ByteBuf content = msg.content();
    }
}
