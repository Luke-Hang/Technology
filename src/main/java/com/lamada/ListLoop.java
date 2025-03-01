package com.lamada;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author xiehang
 * @date 2024/7/24 11:57
 */
public class ListLoop {
    public static void main(String[] args) {

        List<Integer> integerList = new ArrayList<>();
//        list.add(1);
//        list.add(2);
//        list.add(3);
//        list.add(4);
        Collections.addAll(integerList, 1, 2, 3, 4, 5);
//        listFor(integerList);
//        listForFilter(integerList);
        listRemoveIf(integerList);
//        listToMap(integerList);


        String[] arr = {"武汉加油", "中国加油", "世界加油","世界加油"};
        List<String> stringList = new ArrayList<>();
//        listDistinct(stringList);
//        arrDistinct(arr);
//        listFilter(list);
//        streamMatch(list);
//        convertList(stringList);
    }



    private static void listFor(List list) {
        //基本遍历与打印
        list.forEach(item -> System.out.println(item));
        System.out.println("========================");
        //当遍历操作是调用对象的某个方法时，可以使用方法引用简化lambda表达式。
        list.forEach(System.out::println);
    }

    /**
     * 条件判断内操作
     * @param list
     */
    private static void listForFilter(List<Integer> list) {
        list.forEach(item -> {
/*            if (item % 2 == 0) {
                System.out.println(item);
            }*/
            if (item.toString().equals("1")) {
                doSomething(item);
            }
        });
    }

    /**
     * 映射操作后遍历
     * @param list
     */
    private static void listToMap(List<Integer> list) {
//        list.stream().map(item->item.process()
    }

    private static void doSomething(Integer item) {
        System.out.println(item);
    }
    private static void listRemoveIf(List<Integer> integerList) {
        integerList.removeIf(item -> item % 2 == 0);
        integerList.forEach(System.out::println);
    }



    /**
     * 通过 filter() 方法可以从流中筛选出我们想要的元素。
     * @param list
     */
    private static void listFilter(List<String> list) {
/*        list.add("周杰伦");
        list.add("王力宏");
        list.add("陶喆");
        list.add("林俊杰");
        list.add("林更新");*/
        Collections.addAll(list,"周杰伦","王力宏","陶喆","林俊杰","林更新");
/*        Stream<String>  stream= list.stream().filter(element -> element.contains("林"));
        stream.forEach(System.out::println);*/
        list.stream().filter(item -> item.contains("王")).forEach(System.out::println);
    }

    /**
     * 集合去重统计数量
     * @param list
     */
    private static void listDistinct(List<String> list) {
//        list.add("武汉加油");
//        list.add("中国加油");
//        list.add("世界加油");
//        list.add("世界加油");

        Collections.addAll(list,"武汉加油","中国加油","世界加油","世界加油");
        list.stream().distinct().forEach(System.out::println);
        long count = list.stream().distinct().count();
        System.out.println(count);
    }

    /**
     * Stream 类提供了三个方法可供进行元素匹配，它们分别是：
     *  anyMatch()，只要有一个元素匹配传入的条件，就返回 true。
     *  allMatch()，只有有一个元素不匹配传入的条件，就返回 false；如果全部匹配，则返回 true。
     *  noneMatch()，只要有一个元素匹配传入的条件，就返回 false；如果全部不匹配，则返回 true。
     * @param list
     */
    private static void streamMatch(List<String> list) {
        list.add("周杰伦");
        list.add("王力宏");
        list.add("陶喆");
        list.add("林俊杰");

        boolean anyMatchFlag1  = list.stream().anyMatch(element -> element.contains("王"));
        boolean anyMatchFlag2 = list.stream().anyMatch(element -> element.contains("解"));
//        System.out.println(anyMatchFlag1);
//        System.out.println(anyMatchFlag2);

        boolean allMatchFlag1  = list.stream().allMatch(element -> element.length() > 1);
        boolean allMatchFlag2  = list.stream().allMatch(element -> element.length() > 2);
        System.out.println(allMatchFlag1);
        System.out.println(allMatchFlag2);
    }

    private static void convertList(List<String> list) {
        String[] arr = {"武汉加油", "中国加油", "世界加油","世界加油"};
        //数组转集合
        List<String> list1 = Arrays.asList(arr);
        list1.forEach(System.out::println);
        System.out.println("=================================");
        //集合转数组
        String[] arrs = (String[]) list1.toArray();
        Arrays.stream(arrs).forEach(System.out::println);
        System.out.println("=================================");
        //使用流转数组
        String[] strings = list1.stream().toArray(String[]::new);
        Arrays.stream(strings).forEach(System.out::println);
    }
}
