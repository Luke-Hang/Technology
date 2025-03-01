package com.huawei.Memoizer;

import com.sun.scenario.effect.ImageData;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xiehang
 * @create 2022-10-02 16:14
 */
public class Rendere {

    private final ExecutorService executor;

    public Rendere(ExecutorService executor){
        this.executor=executor;
    }

    void renderPage(Character source){

        CompletionService<ImageData> completionService = new ExecutorCompletionService<ImageData>(executor);

        completionService.submit(new Callable<ImageData>() {
            @Override
            public ImageData call() throws Exception {
//                return imageInfo.downloadImage();//并行下载图片
                return null;
            }
        });
    }
}
