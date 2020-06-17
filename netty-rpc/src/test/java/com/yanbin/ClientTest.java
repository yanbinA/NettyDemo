package com.yanbin;

import com.yanbin.client.RpcProxy;
import com.yanbin.client.SomeServer;

/**
 * @author Depp
 */
public class ClientTest {
    public static void main(String[] args) {
        SomeServer instance = RpcProxy.createInstance(SomeServer.class);
        System.out.println(instance.hello("RPC"));
        System.out.println(instance.hello("RPC", "你好"));
    }
}
