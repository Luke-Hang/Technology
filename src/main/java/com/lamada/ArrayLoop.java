package com.lamada;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author xiehang
 * @date 2024/7/24 12:34
 */
public class ArrayLoop {
    public static void main(String[] args) {
        String[] arr = {"1武汉加油", "中国加油", "2武汉加油","世界加油"};
//        arrLoop(arr);
        arrLoopFilter(arr);
//        arrDistinct(arr);
    }

    private static void arrLoopFilter(String[] arr) {
        Arrays.stream(arr).filter(item -> item.contains("武汉")).forEach(System.out::println);
    }

    /**
     * 简单循环
     * @param arr
     */
    private static void arrLoop(String[] arr) {
        Arrays.stream(arr).forEach(item-> System.out.println(item));
        Arrays.stream(arr).forEach(System.out::println);
    }

    private static void arrDistinct(String[] arr) {
        Arrays.stream(arr).distinct().forEach(System.out::println);
        long count = Arrays.stream(arr).distinct().count();
        System.out.println(count);
    }
}
