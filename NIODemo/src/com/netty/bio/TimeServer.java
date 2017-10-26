package com.netty.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by yanbin on 2017/10/18 0018 12:03
 * @author yanbin
 * 同步阻塞I/O服务器
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {

        int port = 8888;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        ServerSocket server = null;

        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port:" + port);
            Socket socket = null;
            while (true) {
                socket = server.accept();
                new Thread(new TimeServerHandler(socket)).start();
            }
        } finally {
            if (server != null) {
                System.out.println("The time server close");
                server.close();
                server = null;
            }
        }

    }

}