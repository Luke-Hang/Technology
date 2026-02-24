package com.test;

/**
 * @author xiehang
 * @date 2025/1/21 17:47
 */
public class Break {


    public static void main(String[] args) {
        for (int i = 0; i <= 3; i++) {
            //continue：跳出当前的这一次循环，继续下一次循环。
            if (i == 1) {
                System.out.println("1");
                continue;
            }
            if (i == 2) {
                System.out.println("2");
            }

            //break：跳出整个循环体。
            if (i == 3) {
                System.out.println("3");
                break;
            }
            if (i == 4) {
                System.out.println("4");
            }
        }

        boolean flag = true;
        //return 跳出所在方法，结束该方法的运行
        if (flag) {
            System.out.println("xixi");
            return;
        }
        System.out.println("haha");
    }
}
