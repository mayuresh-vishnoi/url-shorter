package com.mayur29.urlshortner.utils;

import org.springframework.stereotype.Component;

@Component
public class Base62 {

    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public static String encode(Long value){
        StringBuilder sb = new StringBuilder();
        while (value>0){
            sb.append(CHARSET.charAt((int) (value % 62)));
            value /= 62;
        }

        return sb.toString();
    }
}
