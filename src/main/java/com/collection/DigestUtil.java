package com.collection;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

/**
 * @author xiehang
 * @date 2025/5/9 20:08
 */
public class DigestUtil {


    /**
     * MD5 加密
     */
    @Test
    public void test01(){
        byte[] bytes = DigestUtils.md5("123");
        System.out.println(bytes);
    }

    /**
     * SHA256 加密
     */
    @Test
    public void test02(){
        byte[] bytes = DigestUtils.sha256("123");
        System.out.println(bytes);
    }
}
