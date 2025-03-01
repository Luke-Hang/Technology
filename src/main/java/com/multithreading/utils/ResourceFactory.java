package com.multithreading.utils;

/**
 * @author xiehang
 * @create 2022-10-17 0:34
 *
 * 线程安全的延长初始化占位类模式
 */
public class ResourceFactory {
    private static class ResourceHolder {
        public static Resource resource = new Resource();
    }

    public static Resource getResource() {
        return ResourceHolder.resource;
    }
}
