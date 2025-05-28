package com.example.userauthentication_react_springboot.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userauthentication_react_springboot.model.OtpToken;
import com.example.userauthentication_react_springboot.model.User;
import com.example.userauthentication_react_springboot.repository.OtpTokenRepository;
import com.example.userauthentication_react_springboot.service.MailService;
import com.example.userauthentication_react_springboot.service.OtpTokenService;
import com.example.userauthentication_react_springboot.util.OtpUtil;
import com.example.userauthentication_react_springboot.service.UserService;

@Service
public class OtpTokenServiceImpl implements OtpTokenService {
    private static final Logger logger = LoggerFactory.getLogger(OtpTokenServiceImpl.class);

    private final OtpTokenRepository otpTokenRepository;
    private final MailService mailService;
    private final UserService userService;

    public OtpTokenServiceImpl(OtpTokenRepository otpTokenRepository, MailService mailService, UserService userService){
        this.otpTokenRepository = otpTokenRepository;
        this.mailService = mailService;
        this.userService = userService;
    }

    /**
     * createOtp method takes in user entity as an input, deletes the otp's for the user
     * and sets the otp fields and saves it.
     */


    @Override
    @Transactional
    public void createOtp(User user) {
        try {
            otpTokenRepository.deleteByUser(user);
            otpTokenRepository.flush();
            
            OtpToken otpToken = new OtpToken();
            otpToken.setUser(user);
            otpToken.setEmail(user.getEmail());
            String otp = OtpUtil.generateOtp();
            otpToken.setOtp(otp);
            otpToken.setExpiryTime(LocalDateTime.now().plusMinutes(5));
            
            otpTokenRepository.save(otpToken);

            String subject = "Your OTP code for setting new Password is:";
            String body = "Your OTP code for setting new Password is: " + otp;
            
            mailService.sendMail(user.getEmail(), subject, body);
        } catch (Exception e) {
            logger.error("Failed to create or send OTP for user: {}. Error: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to create or send OTP: " + e.getMessage(), e);
        }
    }

    /*
    * getValidOtp method takes User and otp as input and searches for otp using user and otp.
    * If found it checks if the otp associated with the user is valid from the current point of
    * view. If it is not then it returns Optional.empty();
    * */
    @Override
    public Optional<OtpToken> getValidOtp(User user,String otp) {
        Optional<OtpToken> otpToken = otpTokenRepository.findByUserAndOtp(user, otp);
        return otpToken.filter(token->token.getExpiryTime().isAfter(LocalDateTime.now()));
    }

    /*
    * deleteOtp method takes in user entity as an input and deletes the otp's for the user.
    * */
    @Override
    @Transactional
    public void deleteOtp(User user) {
        otpTokenRepository.deleteByUser(user);
    }

    @Override
    @Transactional
    public boolean isCorrectOtp(String email, String otp) {
        Optional<User> user = userService.findByEmail(email);
        if(user.isEmpty()){
            return false;
        }

        Optional<OtpToken> otpToken = otpTokenRepository.findByUser(user.get());
        if(otpToken.isEmpty()){
            return false;
        }

        if(otpToken.get().getExpiryTime().isBefore(LocalDateTime.now())){
            return false;
        }

        boolean isValid = otpToken.get().getOtp().equals(otp);
        if (isValid) {
            OtpToken token = otpToken.get();
            token.setValidated(true);
            otpTokenRepository.save(token);
        }
        
        return isValid;
    }

    @Override
    public boolean hasValidOtp(User user) {
        Optional<OtpToken> otpToken = otpTokenRepository.findByUser(user);
        if(otpToken.isEmpty()){
            return false;
        }

        return !otpToken.get().getExpiryTime().isBefore(LocalDateTime.now()) && otpToken.get().isValidated();
    }
}
