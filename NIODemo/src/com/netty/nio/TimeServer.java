package com.netty.nio;

/**
 * NIO编程  非阻塞服务端
 * Created by yanbin on 2017/10/26 0026 2:15
 * @author yanbin
 */

public class TimeServer {

    public static void main(String[] args) {

        int port = 9999;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        //3.创建Reactor线程，创建Selector多复路选择器并启动线程
        MultiplexerTimeServer multiplexerTimeServer = new MultiplexerTimeServer(port);
        new Thread(multiplexerTimeServer, "NIO-MultiplexerTimeServer-011").start();


    }

}
