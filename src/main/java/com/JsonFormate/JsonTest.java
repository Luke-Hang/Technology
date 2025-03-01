package com.JsonFormate;


import com.alibaba.fastjson.JSONArray;

import java.util.*;

/**
 * @author xiehang
 * @date 2024/7/25 9:04
 */
public class JsonTest {
    public static void main(String[] args) {
        intListJson();
        stringListJson();
        stringMapJson();

        Student s1 = objectJson();
        Student s2 = objectListJson(s1);
        Map<String, Object> sMap = objectMapJson(s1, s2);
        listMapJson(sMap);
    }


    /**
     * int list json格式：[1,2,3,4,5]
     * [
     *     1,
     *     2,
     *     3,
     *     4,
     *     5
     * ]
     */
    private static void intListJson() {
        // 整型list
        ArrayList<Integer> ids = new ArrayList<>();
        Collections.addAll(ids, 1, 2, 3, 4, 5);
        System.out.println("整型list文本格式：" + ids);
        String idsJson = JSONArray.toJSONString(ids);
        System.out.println("整型list json格式：" + idsJson);
        System.out.println("============================================");
    }

    /**
     * string list json格式： ["1","2","3","4","5"]
     * [
     *     "1",
     *     "2",
     *     "3",
     *     "4",
     *     "5"
     * ]
     */
    private static void stringListJson() {
        // 字符串类型list
        List<String> names = new ArrayList<>();
//        names.add("张三");
//        names.add("李四");
//        names.add("王五");
        Collections.addAll(names, "1", "2", "3", "4", "5");
        System.out.println("字符串类型list文本格式： " + names);
        String namesJson = JSONArray.toJSONString(names);
        System.out.println("字符串类型list json格式： " + namesJson);
        System.out.println("============================================");
    }


    /**
     * 字符串型map文本格式：{score=100, name=王五, id=1, age=21}
     * 字符串型map json格式：{"score":"100","name":"王五","id":"1","age":"21"}
     * 与对象区别, map的key或value的数据类型都是一样的
     * {
     *     "score": "100",
     *     "name": "王五",
     *     "id": "1",
     *     "age": "21"
     * }
     */
    private static void stringMapJson() {
        // 字符串型map
        Map<String, String> StringMap = new HashMap<>();
        StringMap.put("id", "1");
        StringMap.put("name", "王五");
        StringMap.put("age", "21");
        StringMap.put("score", "100");
        System.out.println("字符串型map文本格式：" + StringMap);
        String StringMapJson = JSONArray.toJSONString(StringMap);
        System.out.println("字符串型map json格式：" + StringMapJson);
        System.out.println("============================================");
    }

    /**
     * 实体类文本格式：Student(id=1, name=张三, age=18, score=88.0)
     * 实体类json格式：{"age":18,"id":1,"name":"张三","score":88.0}
     * 格式化结果
     * {
     *     "age": 18,
     *     "id": 1,
     *     "name": "张三",
     *     "score": 88.0
     * }
     */
    private static Student objectJson() {
        // 实体类
        Student s1 = new Student(1, "张三", 18, 88);
        System.out.println("实体类文本格式：" + s1);
        String s1Json = JSONArray.toJSONString(s1);
        System.out.println("实体类json格式：" + s1Json);
        System.out.println("============================================");
        return s1;
    }

    /**
     * 对象类型list文本格式：[Student(id=1, name=张三, age=18, score=88.0), Student(id=2, name=李四, age=20, score=90.0)]
     * 对象类型list json格式：[{"age":18,"id":1,"name":"张三","score":88.0},{"age":20,"id":2,"name":"李四","score":90.0}]
     * [
     *     {
     *         "age": 18,
     *         "id": 1,
     *         "name": "张三",
     *         "score": 88.0
     *     },
     *     {
     *         "age": 20,
     *         "id": 2,
     *         "name": "李四",
     *         "score": 90.0
     *     }
     * ]
     */
    private static Student objectListJson(Student s1) {
        // 对象型list
        ArrayList<Student> students = new ArrayList<>();
        Student s2 = new Student(2, "李四", 20, 90);
//        s2.setId(2);
//        s2.setName("李四");
//        s2.setAge(20);
//        s2.setScore(90);
        students.add(s1);
        students.add(s2);
        System.out.println("对象类型list文本格式："+students);
        String sJson = JSONArray.toJSONString(students);
        System.out.println("对象类型list json格式："+sJson);
        System.out.println("============================================");
        return s2;
    }

    /**
     * 对象型sMap文本格式：
     * {student2=Student(id=2, name=李四, age=20, score=90.0), student1=Student(id=1, name=张三, age=18, score=88.0)}
     * 对象型sMap json格式：
     * {"student2":{"age":20,"id":2,"name":"李四","score":90.0},"student1":{"age":18,"id":1,"name":"张三","score":88.0}}
     *
     * {
     *     "student2": {
     *         "age": 20,
     *         "id": 2,
     *         "name": "李四",
     *         "score": 90.0
     *     },
     *     "student1": {
     *         "age": 18,
     *         "id": 1,
     *         "name": "张三",
     *         "score": 88.0
     *     }
     * }
     * @param s1
     * @param s2
     * @return
     */
    private static Map<String, Object> objectMapJson(Student s1, Student s2) {
        // 对象型map
        Map<String, Object> sMap = new HashMap<>();
        sMap.put("student1", s1);
        sMap.put("student2", s2);
        System.out.println("对象型sMap文本格式："+sMap);
        String sMapJson = JSONArray.toJSONString(sMap);
        System.out.println("对象型sMap json格式："+sMapJson);
        System.out.println("============================================");
        return sMap;
    }

    /**
     * map型list文本格式：
     * [{student2=Student(id=2, name=李四, age=20, score=90.0), student1=Student(id=1, name=张三, age=18, score=88.0)}]
     * map型list json格式：
     * [{"student2":{"age":20,"id":2,"name":"李四","score":90.0},"student1":{"age":18,"id":1,"name":"张三","score":88.0}}]
     * 格式化后
     * [
     *     {
     *         "student2": {
     *             "age": 20,
     *             "id": 2,
     *             "name": "李四",
     *             "score": 90.0
     *         },
     *         "student1": {
     *             "age": 18,
     *             "id": 1,
     *             "name": "张三",
     *             "score": 88.0
     *         }
     *     }
     * ]
     */
    private static void listMapJson(Map<String, Object> sMap) {
        // map型list
        List<Map> MapList = new ArrayList<>();
        MapList.add(sMap);
        System.out.println("map型list文本格式："+MapList);
        String MapListJson = JSONArray.toJSONString(MapList);
        System.out.println("map型list json格式："+MapListJson);
    }
}
