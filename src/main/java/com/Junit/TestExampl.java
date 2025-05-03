package com.Junit;

import com.Equals.Person;
import org.junit.Assert;

import static org.junit.Assert.assertEquals;

/**
 * @author xiehang
 * @date 2025/5/3 10:46
 */
public class TestExampl {

    /**
     * assert BooleanExpr;
     * 如果 BooleanExpr 的计算结果为 true，则不会发生任何事情，并继续执行。
     * 但是，如果表达式计算结果为 false，那么将抛出 AssertionError
     * @return
     */


    public Person getPersonName(){
        Person person = new Person();
        //person.setName("zhangsan");
        assert person.getName() != null : "用户名不能为空";
        return person;
    }

    public Person getPersonAge(){
        Person person = new Person();
        //person.setAge(100);
        assert person.getAge() != null : "用户年龄不能为空";
        return person;
    }
}
