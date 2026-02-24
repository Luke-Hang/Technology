package com.collection;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiehang
 * @date 2025/5/9 11:35
 */
public class StringUtil {
    @Test
    public void test01(){
        String str1 = null;
        String str2 = "";
        String str3 = " ";
        String str4 = "abc";
/*        System.out.println(StringUtils.isEmpty(str1));
        System.out.println(StringUtils.isEmpty(str2));
        System.out.println(StringUtils.isEmpty(str3));
        System.out.println(StringUtils.isEmpty(str4));
        System.out.println("-------------------------------------");*/
/*        System.out.println(StringUtils.isNotEmpty(str1));
        System.out.println(StringUtils.isNotEmpty(str2));
        System.out.println(StringUtils.isNotEmpty(str3));
        System.out.println(StringUtils.isNotEmpty(str4));
        System.out.println("-------------------------------------");*/
/*        System.out.println(StringUtils.isBlank(str1));
        System.out.println(StringUtils.isBlank(str2));
        System.out.println(StringUtils.isBlank(str3));
        System.out.println(StringUtils.isBlank(str4));
        System.out.println("-------------------------------------");*/
        System.out.println(StringUtils.isNotBlank(str1));
        System.out.println(StringUtils.isNotBlank(str2));
        System.out.println(StringUtils.isNotBlank(str3));
        System.out.println(StringUtils.isNotBlank(str4));
    }

    @Test
    public void test02(){
        String str1 = null;

        //String str1 = "1,2,3";
        //会出现空指针异常
        //System.out.println(str1.split(","));

        //不会出现空指针异常
        String[] split = StringUtils.split(str1, ",");
        System.out.println(split);
    }

    @Test
    public void test03(){
        List<String> list = Lists.newArrayList("a", "b", "c");
        System.out.println(list);
    }
}
