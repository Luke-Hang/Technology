package com.ngix;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author xiehang
 * @date 2024/7/24 11:37
 */
public class ServerManager {

    public volatile static Map<String,Integer> serverMap=new HashMap<>();
    static {
        serverMap.put("192.168.1.1", 1);
        serverMap.put("192.168.1.2", 2);
        serverMap.put("192.168.1.3", 3);
        serverMap.put("192.168.1.4", 4);
    }
}
