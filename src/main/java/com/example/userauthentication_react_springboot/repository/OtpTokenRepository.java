package com.example.userauthentication_react_springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.userauthentication_react_springboot.model.OtpToken;
import com.example.userauthentication_react_springboot.model.User;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByUserAndOtp(User user, String otp);
    void deleteByUser(User user);
    Optional<OtpToken> findByUser(User user);
}
