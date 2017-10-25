package com.netty.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * 异步的服务器处理类
 * Created by yanbin on 2017/10/27 17:18
 */
public class AsyncTimeServerHandler implements Runnable{

    private int port;
    CountDownLatch latch;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    public AsyncTimeServerHandler(int port) {
        this.port = port;
        //创建异步服务端通道，并绑定监听端口
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The time server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        //初始化CountDownLatch，其作用是在完成一组正在执行的操作之前，允许当前线程一直阻塞
        latch = new CountDownLatch(1);
        doAccept();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doAccept() {
        //接收客户端连接
        //传递CompletionHandler<AsynchronousSocketChannel, ? super A>类型的handler实例接受accept操作成功的通知消息
        asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
    }
}
