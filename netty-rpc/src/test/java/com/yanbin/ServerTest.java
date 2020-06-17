package com.yanbin;

import com.yanbin.server.RpcServer;

/**
 * @author Depp
 */
public class ServerTest {
    public static void main(String[] args) throws Exception {
        RpcServer.start("com.yanbin.service");
    }
}
