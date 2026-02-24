package com.test;

/**
 * @author xiehang
 * @date 2025/1/24 17:25
 * 计算机CPU核数
 */
public class test2 {
    public static void main(String[] args) {
        int cpuNum = Runtime.getRuntime().availableProcessors();
        System.out.println("该计算机CPU核数为： " + cpuNum);
    }
}
