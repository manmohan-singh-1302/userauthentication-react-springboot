package com.example.userauthentication_react_springboot.config;

import com.example.userauthentication_react_springboot.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

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

        // Skip JWT validation for public endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/") || path.startsWith("/api/users/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader =request.getHeader("Authorization");
//        if(jwt!=null && jwt.startsWith("Bearer ")){
//            jwt = jwt.substring(7);
//        }

        /*
         * If authentication header is null or authenticationHeader does not start with Bearer, then we can skip
         * this filter and return to the next filter in the security chain.
         * */
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // extract jwt token form the authHeader which starts from the 7th index of the string.
        String jwt = authHeader.substring(7);
        try{
            SecretKey key = jwtProvider.getSignInKey();
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
            String username = claims.get("username", String.class);
            String email = String.valueOf(claims.get("email"));

            // If the token is expired, then throw BadCredentialsException "JWT Token expired'.
            if(claims.getExpiration().before(new Date())){
                throw new BadCredentialsException("JWT Token expired.");
            }

            // If the user is not authenticated, then update the securityContext
            if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }catch (Exception e){
            throw new BadCredentialsException("Invalid Jwt Token.....");
        }

        filterChain.doFilter(request, response);
    }
}
