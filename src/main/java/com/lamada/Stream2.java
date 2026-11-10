package com.lamada;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 方法	核心作用	返回类型
 * anyMatch	至少一个满足条件	boolean
 * allMatch	全部满足条件	boolean
 * noneMatch	全部不满足条件	boolean
 * map	数据转换	Stream
 * flatMap	流扁平化	Stream
 * filter	数据过滤	Stream
 * findFirst	获取第一个元素	Optional
 * findAny	获取任意一个元素	Optional
 * reduce	数据归约	T / Optional
 * parallelStream	并行处理	Stream
 */

public class Stream2 {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        List<List<Integer>> lists = Arrays.asList(
                Arrays.asList(1, 2),
                Arrays.asList(3, 4),
                Arrays.asList(5, 6)
        );

        //1. anyMatch()、allMatch()、noneMatch(),这三个方法都用于判断集合中的元素是否满足指定条件，返回 boolean。
        /**
         * anyMatch	    至少一个满足
         * allMatch	    全部满足
         * noneMatch	一个都不满足
         */
            //anyMatch()：只要有一个元素满足条件，就返回 true。
            boolean b = numbers.stream()
                    .anyMatch(n -> n > 5);
            System.out.println(b);//true

            //allMatch()：所有元素都满足条件，才返回 true。
            boolean b1 = numbers.stream()
                    .allMatch(n -> n > 0);//true

            //noneMatch()：所有元素都不满足条件，才返回 true。
            boolean result = numbers.stream()
                    .noneMatch(n -> n < 0);
            System.out.println(result);//true

            //2. map() 和 flatMap() 的区别
            List<String> names = Arrays.asList("Tom", "Jack", "Lucy");
            //map()：将一个元素转换成另一个元素。将 String 元素转换为 Integer 元素
            List<Integer> result1  = names.stream()
                    .map(String::length)
                    .collect(Collectors.toList());
            System.out.println(result1);//输出：[3, 4, 4]

            //flatMap()：将多个 Stream 合并成一个 Stream。
        List<List<Integer>> list = Arrays.asList(
                Arrays.asList(1, 2),
                Arrays.asList(3, 4),
                Arrays.asList(5, 6)
        );

        List<Integer> collect = list.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println(collect);//[1, 2, 3, 4, 5, 6]
        /**
         * 核心区别：
         * map：一对一转换。
         * flatMap：将多个流扁平化，合并成一个流。*
         */


        //4. findFirst() 和 findAny() 的区别
        //findFirst()：获取第一个符合条件的元素。
        Optional<Integer> result2 = numbers.stream()
                .filter(n -> n > 3)
                .findFirst();

        System.out.println(result2.get());//4


        //findAny()：获取任意一个符合条件的元素。
        Optional<Integer> result3 = numbers.stream()
                .filter(n -> n > 3)
                .findAny();

        System.out.println(result3.get());//4

        /**
         * findFirst：强调顺序，返回第一个符合条件的元素。
         * findAny：不保证返回哪个符合条件的元素。
         * 在顺序流中，findAny 通常也会返回第一个元素。
         * 在并行流中，findAny 更灵活，可能返回任意一个符合条件的元素。
         */


        //5. reduce()：归约操作, reduce 用于将多个元素聚合成一个结果，例如求和、求最大值、求乘积
            // 0：初始值。。Integer::sum：累加操作。
            Integer reduce = numbers.stream()
                    .reduce(0, Integer::sum);//21
            //示例2：求最大值
            Optional<Integer> reduce1 = numbers.stream()
                    .reduce(Integer::max);//6
            //示例3：求乘积
            numbers.stream()
                    .reduce(1, (a, c) -> a * c);//720


    }
}
