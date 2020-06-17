package com.yanbin.server;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Depp
 */
public class RpcProvider {
    protected final Map<String, Object> registryMap = new HashMap<>();
    private final List<String> clazzCache = new ArrayList<>();

    public void publish(String packageName) throws Exception {
        //获取指定包下的类
        getProviderClass(packageName);
        //注册实例到注册表
        doRegistry();
    }

    private void getProviderClass(String packageName) {
        URL resource = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        if (resource == null) {
            return;
        }

        File file = new File(resource.getFile());
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File childFile : files) {
            if (childFile.isDirectory()) {
                this.getProviderClass(packageName + "." + childFile.getName());
            } else if (childFile.getName().endsWith(".class")){
                clazzCache.add(packageName + "." + childFile.getName().replace(".class", ""));
            }
        }
    }

    private void doRegistry() throws Exception {
        for (String clazzName : clazzCache) {
            Class<?> aClass = Class.forName(clazzName);
            Class<?>[] aClassInterfaces = aClass.getInterfaces();
            if (aClassInterfaces.length > 0) {
                String interfacesName = aClassInterfaces[0].getName();
                registryMap.put(interfacesName, aClass.newInstance());
            }
        }
    }
}
