package com.example.userauthentication_react_springboot.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.userauthentication_react_springboot.config.JwtProvider;
import com.example.userauthentication_react_springboot.dto.ForgotPassword;
import com.example.userauthentication_react_springboot.dto.LoginDto;
import com.example.userauthentication_react_springboot.dto.OtpValidationRequest;
import com.example.userauthentication_react_springboot.dto.ResetPasswordDto;
import com.example.userauthentication_react_springboot.model.User;
import com.example.userauthentication_react_springboot.service.OtpTokenService;
import com.example.userauthentication_react_springboot.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPassword forgotPassword){
        Optional<User> user = userService.findByEmail(forgotPassword.getRegisteredEmail());
        if(user.isEmpty()){
            return ResponseEntity.status(404).body("Email is not registered");
        }
        otpTokenService.createOtp(user.get());
        return ResponseEntity.ok(forgotPassword.getRegisteredEmail());
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<String> otpValidation(@RequestBody OtpValidationRequest request, @RequestParam String email){
        Optional<User> user = userService.findByEmail(email);
        if(user.isEmpty()){
            return ResponseEntity.status(404).body("Email is not registered");
        }
        
        boolean isCorrect = otpTokenService.isCorrectOtp(email, request.getOtp());
        if(isCorrect){
            return ResponseEntity.ok("OTP validated successfully");
        }
        return ResponseEntity.status(400).body("Invalid OTP.");
    }

    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestBody ResetPasswordDto resetPasswordDto){
        Optional<User> user = userService.findByEmail(email);
        if(user.isEmpty()){
            return ResponseEntity.status(404).body("Email is not registered");
        }

        boolean hasValidOtp = otpTokenService.hasValidOtp(user.get());
        if (!hasValidOtp) {
            return ResponseEntity.status(400).body("Please validate your OTP first");
        }

        if(!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())){
            return ResponseEntity.status(400).body("Passwords do not match");
        }

        String password = resetPasswordDto.getNewPassword();
        if (password.length() < 8) {
            return ResponseEntity.status(400).body("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            return ResponseEntity.status(400).body("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            return ResponseEntity.status(400).body("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            return ResponseEntity.status(400).body("Password must contain at least one number");
        }
        if (!password.matches(".*[@$!%*?&].*")) {
            return ResponseEntity.status(400).body("Password must contain at least one special character (@$!%*?&)");
        }

        try{
            userService.resetPassword(user.get().getId(), resetPasswordDto.getNewPassword());
            otpTokenService.deleteOtp(user.get());
            return ResponseEntity.ok("Password has been reset successfully");
        } catch (Exception e) {
            logger.error("Failed to reset password for user: {}. Error: {}", email, e.getMessage(), e);
            return ResponseEntity.status(400).body("Failed to reset password: " + e.getMessage());
        }
    }
}
