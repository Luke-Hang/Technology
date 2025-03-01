package com.multithreading;

/**
 * @author xiehang
 * @date 2025/1/24 14:59
 * <p>
 * 线程死锁
 */
public class DeadLockDemo {

    private static Object resource1 = new Object();//资源 1
    private static Object resource2 = new Object();//资源 2

    /**
     * 线程 1 通过 synchronized (resource1) 获得 resource1 的锁，然后通过Thread.sleep(1000)休眠 1s
     * 此时线程 2 得到执行然后获取到 resource2 的锁。
     * 当线程 1 和线程 2 都休眠结束后，开始企图请求获取对方的资源，
     * 此时这两个线程就会陷入互相等待的状态，也就产生了死锁。
     *
     * @param args
     */
    public static void main(String[] args) {
        //线程1
        new Thread(() -> {
            synchronized (resource1) {
                System.out.println(Thread.currentThread() + "get resource1");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread() + "waiting get resource2");
                synchronized (resource2) {//线程 A休眠结束后，企图请求获取对方的资源resource2
                    System.out.println(Thread.currentThread() + "get resource2");
                }
            }
        }, "线程 1").start();

        //线程2
/*
        new Thread(() -> {
            synchronized (resource2) {
                System.out.println(Thread.currentThread() + "get resource2");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread() + "waiting get resource1");
                synchronized (resource1) {//线程 B休眠结束后，企图请求获取对方的资源resource1
                    System.out.println(Thread.currentThread() + "get resource1");
                }
            }
        }, "线程 2").start();
*/

        //改造后的线程2
        // 线程 1 首先获得到 resource1 的锁,然后通过Thread.sleep(1000)休眠 1s，此时不会释放resource1 的锁
        // 所以候线程 2 无法获取resource1 的锁
        // 等线程 1 休眠结束后，再去获取 resource2 的监视器锁，可以获取到。
        // 等线程1执行完之后，线程 1 释放了对 resource1、resource2 锁的占用，
        // 线程 2 获取到就可以执行了。这样就破坏了破坏循环等待条件，因此避免了死锁
        new Thread(() -> {
            synchronized (resource1) {//线程 1 首先获得到 resource1 的监视器锁,这时候线程 2 就获取不到resource1
                System.out.println(Thread.currentThread() + "get resource1");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread() + "waiting get resource2");
                synchronized (resource2) {
                    System.out.println(Thread.currentThread() + "get resource2");
                }
            }
        }, "线程 2").start();
    }
}
