package com.multithreading.lamda;

/**
 * @author xiehang
 * @date 2024/8/27 22:15
 */
public class RunnableThreadTest {
    public static void main(String[] args) {

        //创建实现类的对象
        RunnableThread runnableThread = new RunnableThread();

        //将此对象作为参数传递到Thread类的构造器中，创建Thread类的对象
        Thread t1 = new Thread(runnableThread);
        t1.setName("线程1");

        //通过Thread类的对象调用start():① 启动线程 ②调用当前线程的run()-->调用了Runnable类型的target的run()
        t1.start();


        //再启动一个线程，遍历100以内的偶数
        Thread t2 = new Thread(runnableThread);
        t2.setName("线程2");
        t2.start();
    }
}
