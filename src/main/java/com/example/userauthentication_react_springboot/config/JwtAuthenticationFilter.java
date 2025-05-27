package com.example.userauthentication_react_springboot.config;

import java.io.IOException;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.userauthentication_react_springboot.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
* This JwtTokenValidation class extends OncePerRequestFilter i.e., this will run everytime an api is hit. 
* */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(UserService userService, JwtProvider jwtProvider){
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    /*
     *  doFilterInternal method is responsible for updating the SecurityContextHolder of the application. It takes 
     *  request, response, and FilterChain as arguments. When an api call is made, jwt authentication token is sent
     *  along with the request Header named as authorization.
     */
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Skip JWT validation for public endpoints
        if (path.startsWith("/api/auth/") || 
            path.equals("/api/users") || 
            path.equals("/api/users/register") ||
            path.startsWith("/api/users/verify-email/") ||
            path.startsWith("/api/users/reset-password/") ||
            path.startsWith("/api/users/forgot-password/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        
        // If no Authorization header, continue to next filter
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token
            String jwt = authHeader.substring(7);
            SecretKey key = jwtProvider.getSignInKey();
            
            // Parse and validate token
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            String username = claims.get("username", String.class);
            
            // Check token expiration
            if(claims.getExpiration().before(new Date())){
                throw new BadCredentialsException("JWT Token expired.");
            }

            // Set authentication if not already set
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException("Invalid JWT Token: " + e.getMessage());
        }
    }
}
