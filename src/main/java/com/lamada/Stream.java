package com.lamada;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Stream 的操作分类
 * 中间操作	filter、map、sorted、distinct、limit、skip	处理数据，返回新的 Stream
 * 终端操作	collect、forEach、count、reduce、anyMatch	触发执行并产生结果
 *
 * 面试标准回答：
 * Stream API 是 Java 8 提供的一种集合数据处理方式，支持函数式编程。
 * 常见操作包括：
 * - filter：过滤数据。
 * - map：转换数据。
 * - sorted：排序。
 * - distinct：去重。
 * - collect：收集结果。
 * - groupingBy：分组。
 * - reduce：归约计算。
 * Stream 操作分为中间操作和终端操作，中间操作具有惰性执行的特点，只有执行终端操作时才会真正处理数据。
 *
 *
 */
public class Stream {
    public static void main(String[] args) {
        List<Employee> employeeList = new ArrayList<>();
        //1. filter()：过滤数据,筛选工资大于 10000 的员工。
        List<Employee> result = employeeList.stream()
                .filter(e -> e.getSalary() > 10000)
                .collect(Collectors.toList());

        //2. map()：数据转换,提取所有员工的姓名。这里的 map() 是将一个元素转换成另一个元素。
        List<String> nameList = employeeList.stream()
                .map(Employee::getName)
                .collect(Collectors.toList());

        //3.sorted()：排序,按照工资升序排列。
        List<Employee> salarySortedList1 = employeeList.stream()
                .sorted(Comparator.comparing(Employee::getSalary))
                .collect(Collectors.toList());

        //按照工资升序排列。
        List<Employee> salarySortedList2 = employeeList.stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .collect(Collectors.toList());

        //4. distinct()：去重
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3);
        List<Integer> list3 = numbers.stream()
                .distinct()
                .collect(Collectors.toList());

        //5. limit()：限制数量,取前 3 个员工：
        List<Employee> list4 = employeeList.stream()
                .limit(3)
                .collect(Collectors.toList());

        //6.skip()：跳过元素,跳过前 2 个员工：
        List<Employee> list5 = employeeList.stream()
                .skip(3)
                .collect(Collectors.toList());

        //7.forEach()：遍历
        employeeList.stream()
                .forEach(e -> System.out.println(e.getName()));
        employeeList.forEach(e -> System.out.println(e.getDepartment()));

        //8.count()：统计数量
        long count = employeeList.stream()
                .filter(e -> e.getSalary() > 1000)
                .count();

        //9.collect()：收集结果,把 Stream 处理后的数据转换成 List：
        List<String> collect = employeeList.stream()
                .map(Employee::getDepartment)
                .collect(Collectors.toList());

        //10 groupingBy()：分组 按照部门分组：
        Map<String, List<Employee>> collect1 = employeeList.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));

        Map<String, List<Employee>> collect2 = employeeList.stream()
                .collect(Collectors.groupingBy(employee -> employee.getDepartment()));

        /**
         * Stream 的中间操作是惰性执行的，只有遇到终端操作时才真正执行。
         */
//        employees.stream()
//                .filter(e -> e.getSalary() > 10000)
//                .map(Employee::getName);


    }
}
