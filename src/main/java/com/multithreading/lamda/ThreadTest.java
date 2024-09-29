package com.multithreading.lamda;

/**
 * @author xiehang
 * @date 2024/8/27 22:04
 */
public class ThreadTest {
    public static void main(String[] args) {
        //创建Thread类的子类的对象,线程t1
        MyThread t0 = new MyThread();
        t0.start();

        //创建Thread类的子类的对象,线程t2
        MyThread t1 = new MyThread();
        t1.start();
    }
}
