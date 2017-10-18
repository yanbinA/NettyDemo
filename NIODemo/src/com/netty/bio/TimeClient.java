package com.netty.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by yanbin on 2017/10/18 0018 14:08
 * @author yanbin
 * I/O客户端
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8888;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket("127.0.0.1", port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("QUERY TIME ORDER");
            System.out.println("Send order 2 server succeed.");
            String line = in.readLine();
            System.out.println("Now is:" + line);
        } catch (Exception e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException el) {
                    el.printStackTrace();
                }
            }

            if (out != null) {
                out.close();
                out = null;
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException el) {
                    el.printStackTrace();
                }
                socket = null;
            }
        }
    }

}
