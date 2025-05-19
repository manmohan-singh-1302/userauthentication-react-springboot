package com.example.userauthentication_react_springboot.config;

import com.example.userauthentication_react_springboot.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

/*
* This JwtTokenValidation class extends OncePerRequestFilter i.e., this will run everytime an api is hit. 
* */
@Component
public class JwtTokenValidation extends OncePerRequestFilter {

    private final UserService userService;

    public JwtTokenValidation(UserService userService){
        this.userService = userService;
    }

    /**
     *  doFilterInternal method is responsible for updating the SecurityContextHolder of the application. It takes 
     *  request, response and FilterChain as arguments.
     */
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt =request.getHeader("Authorization");
        if(jwt!=null && jwt.startsWith("Bearer ")){
            jwt = jwt.substring(7);
        }
        try{
            SecretKey key = Keys.hmacShaKeyFor(JWT_CONSTANT.SECRET_KEY.getBytes());
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
            String username = claims.get("username", String.class);


            String email = String.valueOf(claims.get("email"));
            if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }catch (Exception e){

        }

        filterChain.doFilter(request, response);
    }
}
