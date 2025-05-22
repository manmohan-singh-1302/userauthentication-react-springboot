package com.example.userauthentication_react_springboot.service;

import com.example.userauthentication_react_springboot.model.OtpToken;
import com.example.userauthentication_react_springboot.model.User;
import com.example.userauthentication_react_springboot.repository.OtpTokenRepository;

import java.util.Optional;

public interface OtpTokenService {
    void createOtp(User user);
    Optional<OtpToken> getValidOtp(User user,String otp);
    void deleteOtp(User user);

    boolean isValidOtp(User user, String enteredOtp);
}
