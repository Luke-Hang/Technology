package com.myspringboot.model;

import lombok.Data;

/**
 * @author xiehang
 * @date 2023/4/14 22:52
 */
public class LectureBeanImpl extends LectureBean{

    private Integer id;

    private String name;

    public LectureBeanImpl() {

    }

    //尽管可以用this调用一个构造器，但却不能调用两个
    public LectureBeanImpl(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public LectureBeanImpl(Integer id, String name,String sex) {
//        this();//this调用本类构造器
        this(id,name);
    }


    //当this调用本类构造器、super调用父类构造器时，不可以同时出现，也就是this()和super()不能同时出现。
/*    public LectureBeanImpl(Integer id, String name) {
//        super();//super调用父类构造器时
        this();//this调用本类构造器
        this(id,name);
        this.id = id;
        this.name = name;
    }*/

    //当this调用本类属性或方法、super调用父类属性或方法时，可以同时出现，也就是this.*和super.*可以同时出现；
/*    public LectureBeanImpl(Integer id, String name) {
        super();//super调用父类属性或方法时
        this.getName();//this调用本类属性或方法
        this.id = id;
        this.name = name;
    }*/


    public String getName() {
        return name;
    }

    //    public static void LectureBean(String lectureName1) {
//        super();
//        this.lectureName = lectureName1;
//    }
//
//    static {
//        super();
//    }


}
