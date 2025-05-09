package com.collection;

import com.Equals.Person;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;

/**
 * @author xiehang
 * @date 2025/5/9 11:52
 */
public class BeanUtil {

    /**
     * 复制对象属性
     */
    @Test
    public void test() {
        Person person = new Person();
        person.setId("1");
        person.setName("张三");
        person.setAge(10);

        Person person2 = new Person();
        person2.setId("2");
        person2.setName("李四");
        person2.setAge(20);

        System.out.println(person);
        System.out.println(person2);

        //将某个对象的所有属性复制到另一个对象
        //将person对象的所有属性复制到person2对象
        System.out.println("==============================");
        BeanUtils.copyProperties(person,person2);
        System.out.println(person);
        System.out.println(person2);

        //将person2对象的所有属性复制到person对象
        System.out.println("==============================");
        BeanUtils.copyProperties(person2,person);
        System.out.println(person);
        System.out.println(person2);
    }

    /**
     *  获取指定类的指定方法
     */
    @Test
    public void test02(){
        Method method = BeanUtils.findDeclaredMethod(Person.class, "getId");
        System.out.println(method.getName());
    }
}
