package com.myspringboot;

/**
 * @author xiehang
 * @create 2022-11-13 17:05
 */
public class Test {
    public static void main(String[] args) {
        String aa = "ab"; // 放在常量池中
        String bb = "ab"; // 从常量池中查找
        String a = new String("ab"); // a 为一个引用
        String b = new String("ab"); // b为另一个引用,对象的内容一样

//        System.out.println(aa.equals(bb));
        System.out.println("42".equals("42.0"));
    }
}
