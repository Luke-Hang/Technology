package com.huawei.Memoizer;

import java.math.BigInteger;

/**
 * @author xiehang
 * @create 2022-09-27 12:00
 */
public interface Computable<A, V> {

    V compute(A arg) throws InterruptedException;
}
