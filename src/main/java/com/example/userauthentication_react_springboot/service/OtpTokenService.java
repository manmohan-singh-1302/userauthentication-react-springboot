package com.example.userauthentication_react_springboot.service;

import com.example.userauthentication_react_springboot.model.User;

public interface OtpTokenService {
    void createOtp(User user);
    boolean isCorrectOtp(String email, String otp);
    boolean hasValidOtp(User user);
    void deleteOtp(User user);
}
