package com.example.userauthentication_react_springboot.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.userauthentication_react_springboot.config.JwtProvider;
import com.example.userauthentication_react_springboot.dto.LoginDto;
import com.example.userauthentication_react_springboot.dto.ResetPasswordDto;
import com.example.userauthentication_react_springboot.model.User;
import com.example.userauthentication_react_springboot.service.OtpTokenService;
import com.example.userauthentication_react_springboot.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final OtpTokenService otpTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtProvider jwtProvider, UserService userService,
                            OtpTokenService otpTokenService){
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
        this.otpTokenService = otpTokenService;
    }

    /*
    * login method, takes in username, password as input in the form of LoginDto object,
    * and performs validations by using userRepository methods and returns a jwt token
    * for client to store.
    * */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        User user = userService.findByUsername(loginDto.getUsername()).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        String jwt = jwtProvider.generateToken(authentication, user.getEmail());
//        System.out.println("Username: " + loginDto.getUsername());
//        System.out.println("Password: " + loginDto.getPassword());
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email){
        Optional<User> user = userService.findByEmail(email);
        if(user.isEmpty()){
            return ResponseEntity.status(404).body("Email is not registered");
        }
        otpTokenService.createOtp(user.get());
        return ResponseEntity.ok(email);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<String> otpValidation(@RequestBody String otp, @RequestParam String email){
        Optional<User> user = userService.findByEmail(email);
        if(user.isEmpty()){
            return ResponseEntity.status(404).body("Email is not registered");
        }
        
        boolean isCorrect = otpTokenService.isCorrectOtp(user.get(), otp);
        if(isCorrect){
            return ResponseEntity.ok("OTP sent to your registered email");
        }
        return ResponseEntity.status(404).body("Invalid OTP.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestBody ResetPasswordDto resetPasswordDto){
        Optional<User> user = userService.findByEmail(email);
        if(user.isEmpty()){
            return ResponseEntity.status(404).body("Email is not registered");
        }
        if(!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())){
            return ResponseEntity.status(400).body("Passwords do not match");
        }
        try{
            userService.resetPassword(user.get().getId(), resetPasswordDto.getNewPassword());
            otpTokenService.deleteOtp(user.get());
            return ResponseEntity.ok("Password has been reset successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to reset password: " + e.getMessage());
        }
    }

}
