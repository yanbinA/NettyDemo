package com.yanbin.service;

import com.yanbin.client.SomeServer;

/**
 * @author Depp
 */
public class SomeServiceImpl implements SomeServer {
    @Override
    public String hello(String name) {
        return "hello!" + name;
    }

    @Override
    public String hello(String name, String content) {
        return name + "! " + content;
    }
}
