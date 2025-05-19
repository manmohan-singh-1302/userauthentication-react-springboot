package com.example.userauthentication_react_springboot.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JwtProvider {
    private final SecretKey key = Keys.hmacShaKeyFor(JWT_CONSTANT.SECRET_KEY.getBytes());

    public String generateToken(Authentication auth){

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        String roles = populateAuthorities(authorities);
        return Jwts.builder()
            .issuedAt(new Date())
            .expiration(new Date(new Date().getTime()+86400000))
            .claim("email", auth.getName())
                .claim("authorities", roles)
            .signWith(key)
            .compact();
    }

    public String

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();
        for(GrantedAuthority authrority: authorities){
            auths.add(authrority.getAuthority());
        }
        return String.join(",", auths);
    }

}
