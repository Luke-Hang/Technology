package com.huawei.Memoizer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiehang
 * @create 2022-09-28 8:37
 * 构建一个并发的缓存组件二
 */
public class Memoizer2<A,V> implements Computable<A,V>{

    private final Map<A,V> cache=new ConcurrentHashMap<>();

    private final Computable<A,V> c;

    //构造方法对变量c初始化
    public Memoizer2 (Computable<A,V> c){
        this.c=c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        //这里使用ConcurrentHashMap代替HashMap，由于ConcurrentHashMap是线程安全的，因此在访问底层Map时就不需要进行同步
        // cache.get(arg);和 cache.put(arg, result)都是在访问底层map
        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }
}
