package com.JsonFormate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author xiehang
 * @date 2025/1/14 19:40
 */
public class StringToBean {
    public static void main(String[] args) {
        String jsonString = "{\"name\":\"John\", \"age\":30}";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            MyBean myBean = objectMapper.readValue(jsonString, MyBean.class);
            System.out.println(myBean);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
