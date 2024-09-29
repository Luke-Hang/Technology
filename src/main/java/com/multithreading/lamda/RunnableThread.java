package com.multithreading.lamda;

/**
 * @author xiehang
 * @date 2024/8/27 22:13
 *
 * 实现Runnable接口
 */
public class RunnableThread implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + ":" + i);
        }
    }
}
