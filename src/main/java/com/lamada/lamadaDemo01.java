package com.lamada;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiehang
 * @date 2025/1/5 21:31
 */
public class lamadaDemo01 {
    public static void main(String[] args) {
        List<String> nameList = Arrays.asList("Alice", "Bob", "Charlie", "David", "Amy");

        //过滤集合长度大于3的字符串
        List<String> list1 = nameList.stream().filter(item -> item.length() > 4).collect(Collectors.toList());
        //list1.forEach(System.out::println);
        System.out.println(list1);

        //将集合中的字母都转为小写字母
        List<String> list2 = nameList.stream().map(item -> item.toLowerCase()).collect(Collectors.toList());
        System.out.println(list2);
        //list2.forEach(System.out::println);

        //打印集合中的元素
        nameList.forEach(System.out::println);
        System.out.println("=================================");
        nameList.forEach(item-> System.out.println(item));
    }
}
