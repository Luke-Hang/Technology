package com.myspringboot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author xiehang
 * @create 2022-08-20 18:44
 */
public class ReflectTest {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, InvocationTargetException {


        // Class.forName(String className)
        // Class类中最常用的方法,getName以 String 的形式返回此 Class 对象所表示的实体（类、接口、数组类、基本类型或 void）名称。

        // Object.class.getName()返回的就是string 类型的
        //通过反射创建MyReflect对象
        Object instance = Class.forName(MyReflect.class.getName()).newInstance();

        //获取对象MyReflect的sayHello()方法
        Method method = instance.getClass().getMethod("sayHello", String.class);

        //通过反射调用sayHello()方法
        method.invoke(instance, "李四");

    }
}
