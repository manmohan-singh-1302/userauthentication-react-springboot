package com.example.userauthentication_react_springboot.controller;

import com.example.userauthentication_react_springboot.config.JwtProvider;
import com.example.userauthentication_react_springboot.dto.LoginDto;
import com.example.userauthentication_react_springboot.dto.RegistrationDto;
import com.example.userauthentication_react_springboot.model.User;
import com.example.userauthentication_react_springboot.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtProvider jwtProvider, UserService userService){
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
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

    public ResponseEntity<> login(@RequestBody RegistrationDto){}


}
