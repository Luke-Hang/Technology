package com.lamada;

import java.util.Optional;

/**
 * Lambda 表达式是 Java 8 引入的一种函数式编程特性，可以简化匿名内部类的写法，让代码更加简洁。
 * Optional 是 Java 8 引入的一个容器类，主要用于处理可能为空的对象，减少 NullPointerException（空指针异常）。
 */
public class Stream3 {
    public static void main(String[] args) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello Java");
            }
        };

        Runnable runnable1 = () -> {
            System.out.println("Hello Java");
        };


        Employee employee=new Employee();

        String name = employee.getName();
        if (name != null) {
            System.out.println(name);
        }

        Optional<String> name1 = Optional.ofNullable(employee.getName());
        name1.ifPresent(System.out::println);

        //示例 2：设置默认值
        String name3 = null;
        String result = Optional.ofNullable(name3)
                .orElse("Unknown");
        System.out.println(result);//Unknown

        //示例 3：orElse 和 orElseGet 的区别

            //orElse()：无论 Optional 是否有值，都会执行默认值表达式。
            String result1 = Optional.ofNullable(name)
                    .orElse("Unknown");

            //orElseGet()：只有 Optional 为空时，才执行 Supplier 提供的逻辑。
            String result2 = Optional.ofNullable(name)
                    .orElseGet(() -> "Unknown");


        /**
         *
         * Optional 常见方法：
         * 方法	作用
         * of()	创建非空 Optional，传入 null 会抛异常
         * ofNullable()	创建允许 null 的 Optional
         * empty()	创建空 Optional
         * isPresent()	判断是否有值
         * ifPresent()	有值时执行指定操作
         * orElse()	没有值时返回默认值
         * orElseGet()	没有值时执行 Supplier 获取默认值
         * orElseThrow()	没有值时抛出异常
         * map()	对内部值进行转换
         *
         *
         */

    }
}
