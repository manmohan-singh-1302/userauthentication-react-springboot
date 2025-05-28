package com.example.userauthentication_react_springboot.util;

import java.security.SecureRandom;

public class OtpUtil {
    private final static SecureRandom random = new SecureRandom();
    public static String generateOtp(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<6;i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
