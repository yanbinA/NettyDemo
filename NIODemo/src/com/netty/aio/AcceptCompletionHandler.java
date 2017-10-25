package com.netty.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 服务端接收通知消息
 * Created by yanbin on 2017/10/27 17:37
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler>{

    /**
     *  接收客户端成功
     * @param result    异步服务端管道
     * @param attachment    服务器处理类
     */
    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
        //再次调用异步服务端管道的 accept方法，用来异步接收其他客户端连接，形成循环
        attachment.asynchronousServerSocketChannel.accept(attachment, this);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //异步读取客户端的请求消息
        //read(ByteBuffer dst, A attachment, CompletionHandler<Integer, ? super A> handler)
        //dst:接收缓冲区，从异步Channel中读取数据
        //attachment:异步Channel携带的附件，通知回调时作为入参使用
        //handler:接收通知回调的业务Handler
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }
}
