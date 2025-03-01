package com.Inject;

/**
 * @author xiehang
 * @date 2024/12/30 21:40
 */
public class Test01 {
    public static void main(String[] args) {

        //构造器注入
        ConstructInject constructInject = new ConstructInject("构造器注入");
        System.out.println(constructInject.getMessage());

        System.out.println();
        //字段注入
        FiledInject filedInject = new FiledInject();
        filedInject.setMessage("字段注入");
        System.out.println(filedInject.getMessage());


        System.out.println();
        //set 方法注入
        SetInject setInject = new SetInject();
        setInject.setMessage("set 方法注入");
        System.out.println(setInject.getMessage());
    }
}
