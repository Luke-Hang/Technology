package com.lamada;

import com.Equals.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiehang
 * @date 2025/5/1 8:08
 */
public class lamadaDemo02 {
    public static void main(String[] args) {
        List<Person> list = new ArrayList<>();
        Person person = new Person("1","张三",20);
        Person person2 = new Person("2","李四",31);
        Person person3 = new Person("3","赵六",40);
        Person person4 = new Person("4","田七",45);
        list.add(person);
        list.add(person2);
        list.add(person3);
        list.add(person4);

        changeAge(list);


    }

    private static void changeAge(List<Person> list) {
        //过滤年龄大于40的人
        List<Person> list1 = list.stream().filter(item -> item.getAge() > 30).collect(Collectors.toList());
        System.out.println(list1);
        System.out.println("=================================");

        //将赵六的年龄改为80
        List<Person> list2 = list.stream().map(item -> {
            if (item.getName().equals("李四")) {
                item.setAge(80);
            }
            return item;
        }).collect(Collectors.toList());
        System.out.println(list2);

        //打集合
        System.out.println("=================================");
        list.forEach(System.out::println);
    }
}
