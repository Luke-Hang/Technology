package com.multithreading.lamda;

/**
 * @author xiehang
 * @date 2024/8/27 22:18
 */
public class InnerClass {


    /**
     * Lambda 语法
     * 每个 Lambda 表达式都遵循以下法则：
     * (parameters) -> expression
     * 或
     * (parameters) ->{ statements; }
     *
     */
    public static void main(String[] args) {
        //匿名内部类第一种，继承Thread类，创建并启动线程一
//        thread1();
        //匿名内部类第一种，继承Thread类，创建并启动线程二
//        thread2();

        //匿名内部类第一种，继承Thread类，创建并启动线程一，使用lamda
        lamdaThread1();
        //匿名内部类第一种，继承Thread类，创建并启动线程二，使用lamda
//        lamdaThread2();

        //匿名内部类第二种，实现Runnable接口,创建并启动线程一
//        RunnableThread3();
        //匿名内部类第二种，实现Runnable接口,创建并启动线程二
//        RunnableThread4();

        //匿名内部类第一种，实现Runnable接口,创建并启动线程一,使用lamda
        lamadaRunnableThread3();
        //匿名内部类第二种，实现Runnable接口,创建并启动线程二,使用lamda
        lamadaRunnableThread4();
    }



    /**
     * 匿名内部类第一种，实现Runnable接口,创建并启动线程一,使用lamda
     */
    private static void lamadaRunnableThread3() {
        new Thread(() -> loopSleep()).start();

/*        new Thread(() -> {
            loopSleep();
        }).start();*/
    }

    /**
     * 匿名内部类第二种，实现Runnable接口,创建并启动线程二,使用lamda
     */
    private static void lamadaRunnableThread4() {
        new Thread(() -> loopSleep()).start();

/*        new Thread(() -> {
            loopSleep();
        }).start();*/
    }


    /**
     * 匿名内部类第二种，实现Runnable接口,创建并启动线程一
     */
    private static void RunnableThread3() {
/*        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                loopSleep();
            }
        });
        thread3.start();*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                loopSleep();
            }
        }).start();
    }
    /**
     * 匿名内部类第二种，实现Runnable接口,创建并启动线程二
     */
    private static void RunnableThread4() {
/*        Thread thread4 = new Thread(new Runnable() {
            @Override
            public void run() {
                loopSleep();
            }
        });
        thread4.start();*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                loopSleep();
            }
        }).start();
    }



    /**
     * 匿名内部类第一种，继承Thread类，创建并启动线程一，使用lamda
     */
    private static void lamdaThread1() {
/*        Thread thread1 = new Thread(() -> {
            loopSleep();
        });
        thread1.start();*/
//        new Thread(() -> loopSleep()).start();

        new Thread(() -> {
            loopSleep();
        }).start();
    }
    /**
     * 匿名内部类第一种，继承Thread类，创建并启动线程二，使用lamda
     */
    private static void lamdaThread2() {
/*        Thread thread2 = new Thread(() -> {
            loopSleep();
        });
        thread2.start();*/
        //(parameters) -> expression
//        new Thread(()-> loopSleep()).start();
        //(parameters) ->{ statements; }
        new Thread(()-> {loopSleep();}).start();
    }




    /**
     *  匿名内部类第一种，继承Thread类，创建并启动线程一
     */
    private static void thread1() {
/*        Thread thread1 = new Thread() {
            public void run() {
                //让当前线程休眠1s
                loopSleep();
            }
        };
        thread1.start();*/

        new Thread() {
            public void run() {
                loopSleep();
            }
        }.start();
    }
    /**
     * 匿名内部类第一种，继承Thread类，创建并启动线程二
     */
    private static void thread2() {
/*        Thread thread2 = new Thread() {
            public void run() {
                loopSleep();
            }
        };
        thread2.start();*/

        new Thread() {
            public void run() {
                loopSleep();
            }
        }.start();
    }

    private static void loopSleep() {
        for (int i = 0; i < 6; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + ":" + i);
        }
    }
}
