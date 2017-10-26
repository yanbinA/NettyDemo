package com.netty.nio;

/**
 * NIO服务器客户端
 * Created by yanbin on 2017/10/26 21:00
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 9999;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        new Thread(new TimeClientHandler("127.0.0.1", port)).start();

    }

}
