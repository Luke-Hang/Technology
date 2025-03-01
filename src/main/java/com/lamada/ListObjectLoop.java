package com.lamada;

import com.Equals.Person;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author xiehang
 * @date 2024/7/24 16:01
 */
public class ListObjectLoop {
    public static void main(String[] args) {
        List<Person> persons = new ArrayList<>();
        persons.add(new Person(2,"lisi",21));
        persons.add(new Person(3,"wangwu",22));
        persons.add(new Person(1,"zhangsan",20));
        persons.add(new Person(4,"zhaoliu",23));

//        removeIf(persons);
        sort(persons);
    }

    /**
     * map条件匹配
     * @param persons
     */
    private static void removeIf(List<Person> persons) {
        persons.removeIf(person -> person.getName().equals("lisi"));
        persons.forEach(System.out::println);
    }

    private static void sort(List<Person> persons) {
        //java8版本之前排序
        persons.sort(new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                //升序
                // return o1.getAge()-o2.getAge();

                //降序
                return o2.getAge()-o1.getAge();
            }
        });
//        persons.forEach(System.out::println);

        //java8排序
//        persons.sort(Comparator.comparingInt(Person::getAge));
//        persons.forEach(System.out::println);

        //升序
//        persons.sort((o1, o2) -> o1.getAge() - o2.getAge());

        //降序
        persons.sort((o1, o2) -> o2.getAge() - o1.getAge());
        persons.forEach(System.out::println);
    }
}
