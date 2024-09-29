package com.multithreading.utils;

/**
 * @author xiehang
 * @create 2022-10-17 0:33
 *
 * 线程安全的提前初始化
 */
public class EagerInitialization {
    private static Resource resource = new Resource();

    public static Resource getResource() {
        return resource;
    }
}
