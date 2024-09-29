package com.lamada;

import com.Equals.Person;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiehang
 * @date 2024/6/27 20:19
 */
public class MapLoop {
    public static void main(String[] args) {

//        mapLoop();
//        replaceAll();
//        updateMap();

        /** map流操作 **/
        reversedMap();
        mapStream();
        stringListToMap();
        objectListToMap2();
    }

    /**
     * 循环map
     */
    private static void mapLoop() {
        Map<String, Integer> map = new HashMap<>();
        map.put("Alice", 90);
        map.put("Bob", 80);
        map.put("Charlie", 95);
//      map.forEach((k, v) -> System.out.println(k + ": " + v));
        map.forEach((name, score) -> System.out.println(name + ":" + score));
    }

    /**
     * replaceAll()方法,使用Lambda表达式替换Map中的所有值
     */
    private static void replaceAll() {
        Map<String, Integer> map = new HashMap<>();
        map.put("Alice", 90);
        map.put("Bob", 80);
        map.put("Charlie", 95);
//        map.forEach((k, v) -> expression);
        map.replaceAll((name, score) -> score + 5);
        map.forEach((name, score) -> System.out.println(name + "=" + score));
        System.out.println(map);
    }

     /**
     * putIfAbsent()方法,使用Lambda表达式在Map中插入键值对，仅当键不存在时才插入
      * absent缺少的
     */
    private static void updateMap() {
    Map<String, Integer> map = new HashMap<>();
        map.put("Bob", 80);
        map.put("Alice", 90);
        map.putIfAbsent("Charlie", 95);
        map.putIfAbsent("Alice", 100);
        System.out.println(map);
    }


    /**
     * 将字符串list转换为Map
     */
    private static void stringListToMap() {
        List<String> list = new ArrayList<>();
        list.add("Mohan");
        list.add("Sohan");
        list.add("Mahesh");

        //Collectors.toMap(k, value)
        Map<String, Integer> map = list.stream().collect(
                //使用Collectors.toMap()收集流中的数据，生成新的Map。
                //list的每个元素item作为map的key,item的长度作为map的value
                Collectors.toMap(item -> item, item -> item.length()));

        Map<String, Integer> collect = list.stream().collect(
                Collectors.toMap(item -> item, item -> item.length()));
//        System.out.println(map);
        System.out.println(collect);
    }

    /**
     * 将对象list转换为Map
     */
    private static void objectListToMap2(){
        List<Person> list = new ArrayList<>();
        list.add(new Person(100, "zhangsan", 20));
        list.add(new Person(200, "lisi", 30));
        list.add(new Person(300, "wangwu", 40));
        //Collectors.toMap(k, value),person.getName()作为map的key,person作为map的value
        Map<String, Person> personMap = list.stream().collect(Collectors.toMap(person -> person.getName(), person -> person));
        System.out.println(personMap);
        System.out.println("=================================================");

        Map<String, Integer> personMap2 = list.stream().collect(Collectors.toMap(person -> person.getName(), person -> person.getAge()));
        Map<String, Integer> personMap3 = list.stream().collect(Collectors.toMap(Person::getName, Person::getAge));
        System.out.println(personMap2);
        System.out.println(personMap3);
    }

    /**
     * map流操作实现反转map
     */
    private static void reversedMap() {
        Map<String, String> map = new HashMap<>();
        map.put("beijing", "北京");
        map.put("tianjin", "天津");
        map.put("xi’an", "西安");
        map.put("西安2", "西安");
        //使用entrySet().stream()获取原Map的所有键值对流,通过collect()将操作结果收集到一个新Map中
        Map<String, String> collect = map.entrySet().stream().collect(
                //使用Collectors.toMap()收集流中的数据，生成新的Map。
                Collectors.toMap(
                        // 新的键，原来的值,// 新的值，原来的键
                        Map.Entry::getValue, Map.Entry::getKey,
                        //若出现相同的键，则使用(oldValue, newValue) -> newValue选择保留新的值
                        //比如{"西安":"xi’an","西安":"西安2"},键相同，选择保留新的值，则"西安":"西安2"被保留
                        (oldValue, newValue) -> newValue));
        Map<String, String> collect1 = map.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (oldValue, newValue) -> newValue));
//        collect.forEach((k,v)->System.out.println("Key: " + k + ", value: " + v));
        System.out.println(collect1);
    }
    /**
     * map流操作
     * {1:1,2:2,3:1,4:2}
     *
     * 1-[1, 3]
     * 2-[2, 4]
     */
    private static void mapStream(){
        Map<Integer,Integer> map=new HashMap<>();
        map.put(1,1);
        map.put(2,2);
        map.put(3,1);
        map.put(4,2);

        //使用entrySet().stream()获取原Map的所有键值对流,通过collect()操作流并收集到一个新Map中
        Map<Integer, List<Integer>> collect = map.entrySet().stream().collect(
                Collectors.groupingBy(
                        Map.Entry::getValue,
                        //使用mapping收集器将每个Entry的键转换为另一个值（在这个例子中，我们将键直接映射为自身），
                        //然后再使用toList收集器将转换后的值收集到一个List中。
                        //简单来说就是将map中的key转换为一个集合
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

        Map<Integer, List<Integer>> collect2 = map.entrySet().stream().collect(
                Collectors.groupingBy(
                        Map.Entry::getValue, Collectors.mapping(
                                Map.Entry::getKey, Collectors.toList())));

//        System.out.println(collect);
        System.out.println(collect2);
        //将map中的key转换为一个集合,输出[1, 2, 3, 4]
//        List<Integer> collect1 = map.entrySet().stream().collect(Collectors.mapping(Map.Entry::getKey, Collectors.toList()));
//        System.out.println(collect1);
    }

    public static void isRequestSignNatureValid(HttpServletRequest req){
        long startTime = System.currentTimeMillis();
        String requestURI = req.getRequestURI();
        String sign = req.getHeader("X-SIGN");
        String timeStamp = req.getHeader("X-TIMESTAMP");
        if (StringUtils.isBlank(sign) || StringUtils.isEmpty(timeStamp)){

        }

        Map<String, String> map = new HashMap<>();
        StringBuilder sb=new StringBuilder();
        //使用map.entrySet().stream()将Map的entry set转换为流
        LinkedHashMap<String, String> linkedHashMap = map.entrySet().stream()
                //使用sorted(Map.Entry.comparingByKey())对流中的元素按照key进行升序排序
                .sorted(Map.Entry.comparingByKey())
                //使用Collectors.toMap()收集流中的元素为一个新的Map
                .collect(Collectors.toMap(
                        //使用Map.Entry::getValue作为新Map的key,使用Map.Entry::getValue作为新Map的value
                        Map.Entry::getValue, Map.Entry::getValue,
                        // 使用(oldValue, newValue) -> newValue作为值合并函数，
                        // 当遇到相同的key时，保留旧的value
                        // 使用LinkedHashMap::new指定新Map的实现类为LinkedHashMap,
                        // 即创建一个新的LinkedHashMap，以保留元素的插入顺序
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        linkedHashMap.forEach((paramName, paramValue) -> {
            try {
                sb.append(paramName).append("=").append(URLEncoder.encode(paramValue, "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
