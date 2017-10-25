package com.netty.aio;

import java.io.IOException;

/**
 * AIO 异步非阻塞客户端
 * Created by yanbin on 2017/10/25 14:06
 */
public class AIOTimeClient {

    public static void main(String[] args) throws IOException {
        int port = 10001;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-AsyncTimeClientHandler-101").start();
    }

}
