package com.lamada;

import com.sun.corba.se.spi.ior.IORTemplate;
import org.hamcrest.Condition;

import java.util.*;

/**
 * @author xiehang
 * @date 2024/7/24 16:26
 */
public class SetLoop {
    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, "31", "22", "43", "12", "34");
//        beforeJava8(set);

        afterJava8(set);
    }

    private static void afterJava8(Set<String> set) {
        //升序
//        Set<String> sortSet =new TreeSet<String>((o1,o2)->o1.compareTo(o2));
        //降序
        TreeSet<String> sortSet = new TreeSet<>((o1, o2) -> o2.compareTo(o1));
        sortSet.addAll(set);
        sortSet.forEach(item-> System.out.println(item));
    }

    private static void beforeJava8(Set<String> set) {
        Set<String> sortSet = new TreeSet<String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // 升序
                return o1.compareTo(o2);
                // 降序
//                return o2.compareTo(o1);
            }
        });
        sortSet.addAll(set);
//        sortSet.forEach(System.out::println);
        sortSet.forEach(item -> System.out.println(item));
    }
}
