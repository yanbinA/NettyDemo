package com.netty.aio;

import java.io.IOException;

/**
 * AIO服务器， 不需要多路复用器Selector 对注册的通道进行轮询操作即可完成异步读写
 * Created by yanbin on 2017/10/24 14:41
 */
public class AIOTimeServer {

    public static void main(String[] args) throws IOException{
        int port = 10001;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        AsyncTimeServerHandler serverHandler = new AsyncTimeServerHandler(port);
        new Thread(serverHandler, "AIO-AsyncTimeServerHandler-001").start();
    }

}
