package com.example.wingstestpaytm;

import java.util.UUID;

public class Helper {

    public static String generateRandomString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }
}
