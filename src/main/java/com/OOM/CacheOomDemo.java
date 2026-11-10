package com.OOM;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟没有容量限制和淘汰机制的本地缓存。
 * 请在独立 JVM 中使用 -Xms64m -Xmx64m 运行。
 */
public class CacheOomDemo {

    // 静态字段持续持有缓存，缓存中的记录不会因循环结束而被回收。
    private static final Map<Long, CacheRecord> CACHE = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("PID: " + ProcessHandle.current().pid());
        System.out.println("每 200 毫秒缓存一条约 1 MB 的记录，按 Ctrl+C 可停止。");

        Runtime runtime = Runtime.getRuntime();
        long id = 0;
        while (true) {
            // 不同的 key 避免覆盖旧记录，模拟缓存不断增长。
            CACHE.put(id, new CacheRecord(id));
            id++;

            long usedMiB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long maxMiB = runtime.maxMemory() / (1024 * 1024);
            System.out.printf("缓存条数=%d，堆已使用=%d MiB，最大堆=%d MiB%n",
                    CACHE.size(), usedMiB, maxMiB);
            Thread.sleep(200);
        }
    }

    private static class CacheRecord {
        private final long id;
        private final byte[] payload = new byte[1024 * 1024];

        private CacheRecord(long id) {
            this.id = id;
        }
    }
}
