package com.multithreading.utils;

/**
 * @author xiehang
 * @create 2022-10-17 0:31
 *
 * 线程安全的延迟初始化
 */
public class SafeLazyInitialization {

    private static Resource resource;

    public synchronized static Resource getInstance() {
        if (resource == null) {
            resource = new Resource();
        }
        return resource;
    }
}
