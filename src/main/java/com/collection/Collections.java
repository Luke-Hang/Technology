package com.collection;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author xiehang
 * @date 2025/5/9 10:05
 */
public class Collections {

    /**
     * 集合排序
     */
    @Test
    public void test01() {
        List<Integer> list = getList();
        System.out.println("排序前" + list);
        java.util.Collections.sort(list);
        System.out.println("排序后" + list);
    }

    /**
     * 获取集合最大最小值
     */
    @Test
    public void test02() {
        List<Integer> list = getList();
        System.out.println("最小值" + java.util.Collections.min(list));
        System.out.println("最大值" + java.util.Collections.max(list));
    }

    /**
     * 判断集合是否为空
     */
    @Test
    public void test03() {
/*        //List<Integer> list = null;
        List<Integer> list = new ArrayList<>();
        System.out.println("返回集合" + fun(list));*/

        List<Integer> list = new ArrayList<>();
        list.add(1);
        System.out.println("返回集合" + fun(list));

    }

    /**
     * 判断集合是否为空
     * @param list
     * @return
     */
    public static List<Integer> fun(List<Integer> list) {
/*        if (list == null || list.size() == 0) {
            return Collections.emptyList();
        }*/
        if (CollectionUtils.isEmpty(list)) {
            System.out.println("传入集合" + list);
            return java.util.Collections.emptyList();
        }

        if (CollectionUtils.isNotEmpty(list)) {
            System.out.println("传入集合" + list);
            list.add(2);
        }
        return list;
    }

    /**
     * 转换为不可修改的集合
     */
    @Test
    public void test04() {
        List<Integer> list = getList();
        //Collections.unmodifiableList(list),将list转换为不可修改的集合,
        List<Integer> list1 = java.util.Collections.unmodifiableList(list);
        list1.add(4);
        System.out.println(list1);
    }

    /**
     * 对两个集合进行操作
     */
    @Test
    public void test05() {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(1);
        list.add(3);

        List<Integer> list2 = new ArrayList<>();
        list2.add(2);
        list2.add(4);
        list2.add(6);
        //获取并集
        Collection union = CollectionUtils.union(list, list2);
        System.out.println("并集" + union);

        //获取交集
        Collection intersection = CollectionUtils.intersection(list, list2);
        System.out.println("交集" + intersection);

        //获取交集的补集
        Collection disjunction = CollectionUtils.disjunction(list, list2);
        System.out.println("交集的补集" + disjunction);

        //获取差集
        Collection subtract = CollectionUtils.subtract(list, list2);
        System.out.println("差集" + subtract);
    }

    @Test
    public void test06() {
        List<Integer> list = new ArrayList<>();
        list.add(2);
    }



    private static List<Integer> getList() {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(1);
        list.add(3);
        return list;
    }
}
