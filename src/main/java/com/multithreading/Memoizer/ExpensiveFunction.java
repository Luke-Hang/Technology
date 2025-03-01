package com.huawei.Memoizer;

import java.math.BigInteger;

/**
 * @author xiehang
 * @create 2022-09-27 12:06
 */
public class ExpensiveFunction implements Computable<String,BigInteger>{

    @Override
    public BigInteger compute(String arg) throws InterruptedException {
        return new BigInteger(arg);
    }
}
