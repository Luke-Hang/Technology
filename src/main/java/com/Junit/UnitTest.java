package com.Junit;

import com.Equals.Person;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author xiehang
 * @date 2025/5/3 10:43
 */
public class UnitTest {

    @Test
    public void test01(){
        System.out.println("test01");
    }

    @Test
    public void test02(){
        System.out.println("test02");
    }

    @Test
    public void test03(){
        System.out.println("test03");
    }
    @Test
    public void test04(){
        int a = 3;
        int b = 3;
        int ab = a + b;
        assertEquals("加法运算结果应为5", 5, ab);
    }


    @Test
    public void testAge(){
        TestExampl testExampl = new TestExampl();
        Person person = testExampl.getPersonName();
        System.out.println(person);
    }

    @Test
    public void testName(){
        TestExampl testExampl = new TestExampl();
        Person person = testExampl.getPersonAge();
        System.out.println(person);
    }
}
