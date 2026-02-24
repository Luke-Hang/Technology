package com.lamada;

import java.util.Arrays;
import java.util.List;

/**
 * @author xiehang
 * @date 2025/1/5 21:31
 */
public class StreamAPI {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        names.stream()
                .filter(name -> name.length() > 4)
                .map(String::toUpperCase)
                .forEach(System.out::println);
    }
}
