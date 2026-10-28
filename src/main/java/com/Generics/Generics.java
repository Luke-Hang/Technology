package com.Generics;

import java.util.ArrayList;
import java.util.List;

public class Generics {

    class Animal {}
    class Dog extends Animal {}
    class Cat extends Animal {}

    public static void main(String[] args) {
        //调用时类型确定，既能读，也能写
        List<Dog> dogs = new ArrayList<>();
        Dog d = getFirst(dogs);

        printAll(dogs);
    }

    /**
     *
     * 写法	                作用	        读	                写	        场景
     * <T>	                定义类型变量	✅	                ✅	        通用工具，返回和入参类型保持一致
     * List<?>	            任意未知类型	✅（只能 Object）	❌	        只读遍历
     * List<? extends T>	T 及其子类	✅（T）	            ❌	        读取数据（生产者）
     * List<? super T>	    T 及其父类	✅（只能 Object）	✅（T）	    写入数据（消费者）
     *
     *`<T>` 固定类型参数，既能 get 元素，也能 add 元素
     * `?` 通配符，未知类型参数，**不能 add 元素，只能 get 元素。
     * `? extends T` 上界通配符,代表 T 或者 T 的子类,适合从集合读取数据；因为无法确定它具体是哪个子类，所以不能往里面新增元素
     * `? super T` 下界通配符， 代表 T 或者 T 的父类，适合往集合写入 T 类型的数据；但读取的时候只能拿到 Object，无法强转成 T。
     *
     * 为什么不能给 List<? extends Animal> add？
     * 因为 List<? extends Animal> 可能是 List<Dog>，也可能是 List<Cat>，编译器无法校验类型安全，直接禁止写入。
     *
     * 为什么 List<? super Dog> 可以 add Dog？
     * 不管它是 List<Dog> / List<Animal> / List<Object>，Dog 一定是它的子类，类型安全。
     *
     *
     */

    // 声明T：这个方法里T是一个固定类型，既能读，也能写
    // 可以传进来一个固定类型的 list,也可以添加固定类型的元素
    public static <T> T getFirst(List<T> list) {
        return list.get(0);
    }

    // 编译报错！不知道是什么类型，不能add，任何泛型 List 都能传进来，**只能读，不能新增元素**
    // 可以传入任何类型的 List ，但是不能写入新的元素，因为不知道是什么类型
    // 场景：只遍历、只读，不修改集合
    public static void printAll(List<?> list) {
        Object object = list.get(0);
        for (Object o : list) {
            System.out.println(o);
        }
         list.add(new Dog()); // ❌ 编译报错！不知道是什么类型，不能add
    }

    // 集合里是 Animal 或者 Animal 的子类
    // 适合：**生产者，往外拿数据**
    public void readAnimal(List<? extends Animal> list) {
        Animal a = list.get(0); // ✅ 读取，拿到Animal
        list.add(new Dog()); // ❌ 不能添加！不知道是List<Dog>还是List<Cat>
    }


    // 含义：集合类型是 Dog，或者 Dog 的父类（Animal、Object）
    // ：**消费者，往里面放数据**
    public void addDog(List<? super Dog> list) {
        list.add(new Dog()); // ✅ 可以存Dog
        Dog d = list.get(0); // ❌ 取出来只能是Object，不能确定是Dog
    }


}

