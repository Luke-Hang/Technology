package com.huawei.Memoizer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiehang
 * @create 2022-09-27 12:03
 * 构建一个并发的缓存组件一
 */
public class Memoizer1<A, V> implements Computable<A, V> {

    private final Map<A, V> cache = new HashMap<>();

    private final Computable<A,V> c;

    //构造方法对变量c初始化
    public Memoizer1(Computable<A, V> c) {
        this.c = c;
    }

    /**
     * @param arg
     * @return
     * @throws InterruptedException
     *
     * 第一种尝试：使用HashMap来保存之前计算的结果。compute方法将首先检查需要的结果是否已经在缓存中，如果存在则返回之前计算的值。
     * 否则，将把计算结果缓存在HashMap中，然后再返回。
     */
    @Override
    //使用synchronized对整个compute方法进行同步
    public synchronized V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }
}
