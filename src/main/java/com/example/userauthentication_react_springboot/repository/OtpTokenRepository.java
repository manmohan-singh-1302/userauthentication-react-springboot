package com.example.userauthentication_react_springboot.repository;

import com.example.userauthentication_react_springboot.model.OtpToken;
import com.example.userauthentication_react_springboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByUserandOtp(User user, String otp);
    void deleteByUser(User user);
}
