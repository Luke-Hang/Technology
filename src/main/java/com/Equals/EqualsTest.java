package com.Equals;

import com.Equals.Person;

/**
 * @author xiehang
 * @date 2024/8/26 11:56
 */
public class EqualsTest {
    public static void main(String[] args) {
        doubleEqualSymbol();
        equalSymbol();
    }

    private static void doubleEqualSymbol() {
        String a = new String("ab"); // a 为一个引用
        String b = new String("ab"); // b为另一个引用,对象的内容一样

        String aa = "ab"; // 放在常量池中
        String bb = "ab"; // 从常量池中查找

        System.out.println(aa == bb);//true 放在常量池中,从常量池中查找
        System.out.println(a == b);//false, String为引用数据类型，比较内存地址值
        System.out.println(a.equals(b));//true, String中重写了equals方法，a和b的属性都为"ab"
        System.out.println(42 == 42.0);//基本数据类型，比较值，为true
    }

    private static void equalSymbol() {
        //Person重写equals，比较两个对象中的属性是否相等；属性相等，返回 true
        Person person1 = new Person(1, "zhangsan", 20);
        Person person2 = new Person(1, "zhangsan", 20);
        Person person3 = new Person(1, "zhangsan", 30);

        //People没有重写equals，比较该类的两个对象时，等价于通过“==”比较这两个对象，使用的默认是 Object类equals()方法。
        //因为对象是引用数据类型，所以比较的是对象的地址值，永远为false
        People people1 = new People(1, "zhangsan", 20);
        People people2 = new People(1, "zhangsan", 20);
        People people3 = new People(1, "zhangsan", 30);

//        People people2 = new People();
//        People people3 = new People();

        //person1和person2 属性都相等，返回 true
        System.out.println("person1 equals person2: " + person1.equals(person2));//true
        //person2和person3 age属性不相同，返回 false
        System.out.println("person2 equals person3: " + person2.equals(person3));//false

        //people1和people2 属性都相等，返回 false
        System.out.println("people1 equals people2: " + people1.equals(people2));//false
        //people2和people3 属性都相等，返回 false
        System.out.println("people2 equals people3: " + people2.equals(people3));
    }
}
