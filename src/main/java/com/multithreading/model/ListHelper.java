package com.huawei.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author xiehang
 * @create 2022-09-26 14:33
 */
public class ListHelper<E> {

    public List<E> list=Collections.synchronizedList(new ArrayList<>());

    public boolean putIfAbsen(E x){
        boolean absent = list.contains(x);
        if (absent){
            list.add(x);
            return absent;
        }
        return absent;
    }
}
