package com.yanbin.client;

import com.yanbin.model.InvokeMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Depp
 */
public class RpcProxy {
    public static <T> T createInstance(Class<?> tClass) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (Object.class.equals(method.getDeclaringClass())) {
                            return this.invoke(proxy, method, args);
                        }
                        return rpcInvoke(tClass, method, args);
                    }
                });
    }

    private static Object rpcInvoke(Class<?> tClass, Method method, Object[] args) throws Exception {
        InvokeMessage message = new InvokeMessage();
        message.setClassName(tClass.getName());
        message.setMethodName(method.getName());
        message.setParamTypes(method.getParameterTypes());
        message.setParamValues(args);
        return rpcInvoke(message);
    }

    private static Object rpcInvoke(InvokeMessage message) throws Exception {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        ClientHandle clientHandle = new ClientHandle();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE,
                                    ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new ObjectEncoder());

                            pipeline.addLast(clientHandle);
                        }
                    });
            ChannelFuture future = bootstrap.connect("localhost", 8888).sync();
            future.channel().writeAndFlush(message);
            future.channel().closeFuture().sync();
        } finally {
            loopGroup.shutdownGracefully();
        }
        return clientHandle.result;
    }
}
