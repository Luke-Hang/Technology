package com.test;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xiehang
 * @date 2024/7/30 16:25
 */
public class Test {
    public static void main(String[] args) {

//        String str = null;
//        String str = "";
        String str = "  ";
//        String str = "test";
        test(str);
    }

    /**
     * 总结：
     *  isEmpty 只检查字符串是null或者长度为0。
     *  isBlank 检查字符串是null或者含空白字符。
     * 使用场景建议：
     *  如果你想检查一个字符串是否完全为空（即没有实际内容），你应该使用 isEmpty。
     *  如果你需要确保一个字符串不仅不为空，而且不是由空白字符组成的，那么应该使用 isBlank。
     * @param str
     */
    private static void test(String str) {

        if (StringUtils.isEmpty(str)) {
            System.out.println("str is empty");
        }

        if (StringUtils.isBlank(str)) {
            System.out.println("str is blank");
        }
    }
}
