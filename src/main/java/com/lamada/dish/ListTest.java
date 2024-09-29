package com.lamada.dish;

import com.lamada.dish.Dish;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author xiehang
 * @date 2024/6/6 23:32
 */
public class ListTest {
    public static void main(String[] args) {
        List dishList=new ArrayList<>();
        beforeJava7(dishList);
        afterJava8(dishList);
//        fruits();
//        removeIf();
        replaceAll();

    }

    /**
     * 对数据库查询到的菜肴进行一个处理：
     * 筛选出卡路里小于 400 的菜肴
     * 对筛选出的菜肴进行一个排序
     * 获取排序后菜肴的名字
     * @param dishList
     * @return
     */
    private static List<String> beforeJava7(List<Dish> dishList) {
        List<Dish> lowCaloricDishes = new ArrayList<>();

        //1.筛选出卡路里小于400的菜肴
        for (Dish dish : dishList) {
            if (dish.getCalories() < 400) {
                lowCaloricDishes.add(dish);
            }
        }

        //2.对筛选出的菜肴进行排序
        Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
            @Override
            public int compare(Dish o1, Dish o2) {
                return Integer.compare(o1.getCalories(), o2.getCalories());
            }
        });

        //3.获取排序后菜肴的名字
        List<String> lowCaloricDishesName = new ArrayList<>();
        for (Dish d : lowCaloricDishes) {
            lowCaloricDishesName.add(d.getName());
        }

        return lowCaloricDishesName;
    }
    private static List<String> afterJava8(List<Dish> dishList) {
        return dishList.stream().
                        filter(dish -> dish.getCalories()<400)//筛选出卡路里小于400的菜肴
                        .sorted(Comparator.comparing(Dish::getCalories))//根据卡路里进行排序
                        .map(Dish::getName) //提取菜肴名称
                        .collect(Collectors.toList());//转换为List
    }

    private static void afterJava9(List<Dish> dishList) {
        List<Dish> collect = dishList.stream().filter(dish -> dish.getCalories() > 400).collect(Collectors.toList());
    }

    //使用 forEach() 方法可以方便地遍历集合中的元素，并对每个元素执行自定义操作，从而简化了对集合的处理过程
    //stream()方法,返回一个顺序流，用于对集合中的元素进行顺序操作
    private static void fruits() {
        List<String> fruits = Arrays.asList("Apple", "Banana", "Orange");
        //forEach()方法
        fruits.forEach(fruit-> System.out.println("I like "+fruit));
        System.out.println("======================================");
        //stream()方法
        fruits.stream().forEach(fruit-> System.out.println("I like "+fruit));

        fruits.forEach(System.out::println);
    }

    //removeIf() 方法,使用Lambda表达式来移除集合中满足特定条件的元素
    private static void removeIf() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        // 移除所有偶数
        boolean b = list.removeIf(element -> element % 2 == 0);
        list.forEach(System.out::println);
    }

    //replaceAll()方法
    private static void replaceAll() {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        numbers.replaceAll(e->e*2);// 将列表中的每个元素乘以2
        numbers.forEach(System.out::println);
    }
}
