package com.lamada;

import java.util.*;

/**
 * @author xiehang
 * @date 2024/6/27 20:41
 */
public class unmodifiableList {
    public static void main(String[] args) {

        // Java的Collections工具类提供了一系列的unmodifiableXXX()方法，
        // 可以将一个可变集合转换为一个只读（不可修改）的集合视图
        // 例如，unmodifiableList(), unmodifiableSet(), 和 unmodifiableMap()
        // 任何尝试修改unmodifiableList的操作都会抛出UnsupportedOperationException异常。
//        unmodifiableLists();
//        unmodifiableMap();
        unmodifiableSets();
    }

    /**
     * unmodifiableMap
     * 任何尝试修改unmodifiableList的操作都会抛出UnsupportedOperationException异常。
     */
    private static void unmodifiableMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "2");
        map.put(1, "3");
        System.out.println("=====unmodifiableMap error=====");
        Map<Integer, String> map1 = Collections.unmodifiableMap(map);
        map1.put(1, "3");
        System.out.println(map1);
    }

    /**
     * unmodifiableList
     * 任何尝试修改unmodifiableList的操作都会抛出UnsupportedOperationException异常。
     */
    private static void unmodifiableLists() {
        List<String> originalList = new ArrayList<>();
        originalList.add("item");
        System.out.println("=====unmodifiableList error=====");
        List<String> unmodifiableList = Collections.unmodifiableList(originalList);
        unmodifiableList.add("item1");
    }

    /**
     * unmodifiableSet
     */
    private static void unmodifiableSets() {
        Set<String> set = new HashSet<>();
        set.add("item");
        Set<String> set1 = Collections.unmodifiableSet(set);
        set1.add("item1");
    }
}
