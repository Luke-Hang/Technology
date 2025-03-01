package com.lamada.dish;

import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author xiehang
 * @date 2025/1/5 21:47
 */
public class JavaTime {
    public static void main(String[] args) {
        oldFormat();
        newFormat();
    }


    private static void oldFormat() {
        Date now = new Date();
        System.out.println(now);
        //format yyyy-MM-dd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(now);
        System.out.println(date);

        //format HH:mm:ss
        SimpleDateFormat sdft = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdft.format(now));

        //format yyyy-MM-dd HH:mm:ss
        SimpleDateFormat sdfdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdfdt.format(now));
    }

    private static void newFormat() {
        //format yyyy-MM-dd
        LocalDate date = LocalDate.now();
        System.out.println(date);

        //format HH:mm:ss
        LocalTime localTime = LocalTime.now().withNano(0);
        System.out.println(localTime);

        //format yyyy-MM-dd HH:mm:ss
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String format = dateTime.format(dateTimeFormatter);
        System.out.println(format);
    }


}
