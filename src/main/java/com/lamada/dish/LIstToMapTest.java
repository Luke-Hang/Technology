package com.lamada.dish;

import com.lamada.Type;
import com.lamada.dish.Dish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author xiehang
 * @date 2024/6/7 9:54
 */
public class LIstToMapTest {
    public static void main(String[] args) {
        List<Dish> dishList = new ArrayList<>();
        beforeJDK8(dishList);
        afterJDK8(dishList);
    }

    /**
     * 常规写法
     * @param dishList
     * @return
     */
    private static Map<Type, List<Dish>> beforeJDK8(List<Dish> dishList) {
        Map<Type, List<Dish>> result = new HashMap<>();
        //不存在则初始化
        // result.get(dish.getType())从结果result根据菜系type取出对应的菜系集合
        //result.get(dish.getType()) == null result中没有该菜系集合，
        // 新建一个菜系集合dishes，并将其以key为type加入result
        //result.put(dish.getType(), dishes);将该菜系加入到map集合中
        for (Dish dish : dishList) {
            if (result.get(dish.getType()) == null) {
                List<Dish> dishes = new ArrayList<>();
                dishes.add(dish);
                result.put(dish.getType(), dishes);
            } else {
                //存在则追加
                //result.get(dish.getType())取出对应的菜系
                //.add(dish)将该菜肴加入到该菜系中
                result.get(dish.getType()).add(dish);
            }
        }
        return result;
    }

    /**
     * 使用lamada,对数据库查询到的菜肴根据菜肴种类进行分类，返回一个 Map<Type, List> 的结果
     * {
     *     川菜:[麻辣豆腐，鱼香肉丝],
     *     山菜:[莜面栲栳栳，刀削面]
     * }
     * @param dishList
     */
    private static Map<Type, List<Dish>> afterJDK8(List<Dish> dishList) {
        Map<Type, List<Dish>> collect = dishList.stream().collect(groupingBy(Dish::getType));
        return collect;
    }
}

