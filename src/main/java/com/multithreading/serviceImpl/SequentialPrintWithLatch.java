package com.multithreading.serviceImpl;

import java.util.concurrent.CountDownLatch;

/**
 * 通过CountDownLatch实现三个线程按顺序打印"A"、"B"、"C"
 */
public class SequentialPrintWithLatch {
    public static void main(String[] args) {
        //通过两个 CountDownLatch 对象控制线程执行顺序：
        //latchB：用于阻塞线程2，直到线程1完成打印"A"。
        //latchC：用于阻塞线程3，直到线程2完成打印"B"。
        CountDownLatch latchB = new CountDownLatch(1);
        CountDownLatch latchC = new CountDownLatch(1);


        new Thread(() -> {
            //线程1（打印"A"）
            System.out.println("A");
            //调用 latchB.countDown()，通知等待在 latchB 上的线程（即线程2）继续执行
            latchB.countDown();
        }).start();


        new Thread(() -> {
            try {
                //待线程1完成
                latchB.await();
                //线程2（打印"B"）
                System.out.println("B");
                // 通知线程3可以继续
                latchC.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                // 等待线程2完成
                latchC.await();
                // 线程3（打印"C"）
                System.out.println("C");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
