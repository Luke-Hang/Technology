package com.multithreading.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "data-sync.thread-pool")
public class DataSyncThreadPoolProperties {

    private Integer coreSize;

    private Integer maxSize;

    private Integer queueCapacity = 500;

    private Integer keepAliveSeconds = 60;

    public int resolveCoreSize() {
        return coreSize == null ? Runtime.getRuntime().availableProcessors() : coreSize;
    }

    public int resolveMaxSize() {
        return maxSize == null ? 2 * resolveCoreSize() : maxSize;
    }
}
