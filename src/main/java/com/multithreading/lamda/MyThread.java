package com.multithreading.lamda;

/**
 * @author xiehang
 * @date 2024/8/27 22:02
 *
 * 继承于Thread类
 */
public class MyThread extends Thread{
    @Override
    public void run(){
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + ":" + i);
        }
    }
}
