package com.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO 处理异步连接和读写操作
 * Created by yanbin on 2017/10/26 20:58
 * @author yanbin
 */
public class TimeClientHandler implements Runnable{

    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public TimeClientHandler(String host, int port) {
        this.host = host == null ? "127.0.0.1" : host;
        this.port = port;

        try {
            //1.打开SocketChannel，绑定客户端本地地址
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            //2.设置SocketChannel为非阻塞模式，同时设置客户端连接的TCP参数
            socketChannel.configureBlocking(false);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }







    }

    /**
     * 6.创建Reactor线程，创建多路复用器并启动线程
     */
    @Override
    public void run() {
        try {
            //3.异步连接服务端
            doConnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (!stop) {
            try {
                //7.多路复用器在线程run方法中无限循环体内轮询准备就绪的Key
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    System.out.println("Client-多复路选择器Selector 轮询到了准备就绪的Key");
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭资源
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * 8.接收connect事件进行处理
     * @param key key
     */
    private void handleInput(SelectionKey key) throws IOException{
        if (key.isValid()) {
            //9.判断连接结果，如果连接成功，注册读事件到多路复用器
            System.out.println("接收connect事件进行处理");
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable()) {
                if (sc.finishConnect()) {
                    //10.注册读事件到多路复用器
                    sc.register(selector, SelectionKey.OP_READ);
                    doWrite(sc);
                } else {
                    System.exit(1);
                }
            }
            if (key.isReadable()) {
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                //11.异步读取服务端响应消息到缓冲区
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {
                    //12.对ByteBuffer进行编码，如果有半包消息接受缓冲区Reset，继续读取后续内容，将解码成功的消息封装成Task，投递到业务线程池，进行逻辑处理
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");

                    System.out.println("Now is :" + body);
                    this.stop = true;
                } else if (readBytes < 0) {
                    //关闭对端链路
                    key.cancel();
                    sc.close();
                } else {
                    ;//读到0字节，不处理
                }
            }
        }
    }

    /**
     * 3.异步连接服务端
     */
    private void doConnect() throws IOException{

        //4.判断是否连接成功，如果成功，直接注册读状态位到多路复用器中；发送请求消息，读应答
        if (socketChannel.connect(new InetSocketAddress(host, port))) {
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        } else {
//        异步连接，返回false，说明客户端已发送sync包，服务端没有返回ack包，物理链路还没有建立
//        5.如果没有成功，向Reactor线程的多路复用器注册OP_CONNECT事件,监听服务端的TCP ACK应答
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    /**
     * 发送请求消息到服务端
     * @param socketChannel 客户端通道
     */
    private void doWrite(SocketChannel socketChannel) throws IOException{

        byte[] bytes = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        //改变writeBuffer的模式，写->读
        writeBuffer.flip();
        //13.将POJO对象encode成ByteBuffer，调用SocketChannel的异步write接口将消息异步发送给服务端
        socketChannel.write(writeBuffer);

        if (!writeBuffer.hasRemaining()) {
            System.out.println("Send order 2 server succeed");
        }
    }
}
