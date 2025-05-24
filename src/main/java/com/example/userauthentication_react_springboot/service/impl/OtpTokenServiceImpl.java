package com.example.userauthentication_react_springboot.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.userauthentication_react_springboot.model.OtpToken;
import com.example.userauthentication_react_springboot.model.User;
import com.example.userauthentication_react_springboot.repository.OtpTokenRepository;
import com.example.userauthentication_react_springboot.service.MailService;
import com.example.userauthentication_react_springboot.service.OtpTokenService;
import com.example.userauthentication_react_springboot.util.OtpUtil;

@Service
public class OtpTokenServiceImpl implements OtpTokenService {

    private final OtpTokenRepository otpTokenRepository;
    private final MailService mailService;

    public OtpTokenServiceImpl(OtpTokenRepository otpTokenRepository, MailService mailService){
        this.otpTokenRepository = otpTokenRepository;
        this.mailService = mailService;
    }

    /**
     * createOtp method takes in user entity as an input, deletes the otp's for the user
     * and sets the otp fields and saves it.
     */


    @Override
    public void createOtp(User user) {

        otpTokenRepository.deleteByUser(user);
        OtpToken otpToken = new OtpToken();
        otpToken.setUser(user);
        otpToken.setEmail(user.getEmail());
        otpToken.setOtp(OtpUtil.generateOtp());
        otpToken.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpTokenRepository.save(otpToken);

        String subject = "Your OTP code for setting new Password is:";
        String body = "Your OTP code for setting new Password is: "+ otpToken;
        mailService.sendMail(user.getEmail(), subject, body);
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
    public void deleteOtp(User user) {
        otpTokenRepository.deleteByUser(user);
    }

    @Override
    public boolean isCorrectOtp(User user, String enteredOtp){
        Optional<OtpToken> otpToken = otpTokenRepository.findByUserAndOtp(user, enteredOtp);
        return otpToken.filter(token -> token.getExpiryTime().isAfter(LocalDateTime.now())).isPresent();
    }
}
