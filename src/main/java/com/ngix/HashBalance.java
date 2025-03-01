package com.ngix;

import java.util.*;

/**
 * @author xiehang
 * @date 2024/7/24 11:37
 *有如下四台服务器
 * 服务器地址	    权重
 * 192.168.1.1	1
 * 192.168.1.2	2
 * 192.168.1.3	3
 * 192.168.1.4	4
 */
public class HashBalance {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            String server = getServer("ip"+new Random().nextInt(1000));
            System.out.println(server);
        }
    }

    private static String getServer(String sourceIp) {
        if (sourceIp == null) {
            return "";
        }
        Set<String> serverSet = ServerManager.serverMap.keySet();
        List<String> serverList = new ArrayList<>(serverSet);
        Integer index = sourceIp.hashCode() % serverSet.size();
        return serverList.get(index);
    }
}
