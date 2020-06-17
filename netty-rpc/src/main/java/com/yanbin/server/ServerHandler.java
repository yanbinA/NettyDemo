package com.yanbin.server;

import com.yanbin.model.InvokeMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;

/**
 * @author Depp
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    private Map<String, Object> registryMap;

    public ServerHandler(Map<String, Object> registryMap) {
        this.registryMap = registryMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        InvokeMessage message = (InvokeMessage) msg;
        Object result = "没有改方法";
        if (registryMap.containsKey(message.getClassName())) {
            Object provider = registryMap.get(message.getClassName());
            result = provider.getClass().getMethod(message.getMethodName(), message.getParamTypes())
                    .invoke(provider, message.getParamValues());
        }
        ctx.channel().writeAndFlush(result);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
