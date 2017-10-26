package com.netty.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by yanbin on 2017/10/26 0026 4:34
 */
public class MultiplexerTimeServer implements Runnable{

    private Selector selector;

    private ServerSocketChannel serverChannel;

    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {

        try {
            //1.打开ServerSocketChannel，用于监听客户端连接，是所有客户端连接的父管道
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            //2.绑定监听地址，设置连接为非阻塞模式；Channel支持阻塞和非阻塞两种模式，
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);

            //4.将ServerSocketChannel注册到Reactor线程的多复路选择器Selector上，并监听ACCEPT事件
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The Time Server is Ready in port :" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {

        while (!stop) {
            try {
                //5.多复路选择器Selector在run方法中轮询准备就绪的Key
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()) {
                    System.out.println("多复路选择器Selector 轮询到了准备就绪的Key");
                    key = iterator.next();
                    iterator.remove();
                    try {
                        //处理客户端连接
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.channel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //释放资源，
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException{
        if (key.isValid()) {
            //处理新接入的请求消息
            if (key.isAcceptable()) {
                //6.Selector监听到有新的客户端接入，处理新的请求，完成TCP三次握手，建立物理链路
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = ssc.accept();
                //7.设置客户端链路为非阻塞模式
                socketChannel.configureBlocking(false);
                //8.将新接入的客户端注册到Reactor线程的Selector上，监听读操作，读取客户端发送的网络消息
                socketChannel.register(selector, SelectionKey.OP_READ);
            }

            if (key.isReadable()) {
                //9.异步读取客户端请求消息到Buffer
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(8);
                int read = sc.read(readBuffer);
                if (read > 0) {
                    //读取到内容，进行编码
                    //10.对Buffer进行编解码，如果有半包消息指针reset，继续读取后续的内容，将解码成功的消息封装成Task，
                    //投递到业务线程池，进行业务处理
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");

                    System.out.println("The time server receive order : " + body);

                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";

                    doWrite(sc, currentTime);
                } else if (read < 0) {
                    //关闭链路
                    key.channel();
                    sc.close();
                } else {
                    ;//不做处理
                }
            }

        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException{
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            //11.将POJO对象encode成ByteBuffer，调用write接口，将消息异步发送给客户端
            ByteBuffer write = ByteBuffer.allocate(bytes.length);
            write.put(bytes);
            write.flip();
            channel.write(write);
        }
    }
}
