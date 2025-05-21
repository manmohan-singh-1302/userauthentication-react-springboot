package com.example.userauthentication_react_springboot.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtProvider {
    private final SecretKey key = Keys.hmacShaKeyFor(JWT_CONSTANT.SECRET_KEY.getBytes());

    public String generateToken(Authentication auth, String email){

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        String roles = populateAuthorities(authorities);
        return Jwts.builder()
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis()+86400000))
            .claim("username", auth.getName())
                .claim("email", email)
                .claim("authorities", roles)
            .signWith(getSignInKey())
            .compact();
    }


/*
* This will provide the security key for jwt token verification.
* */

    public SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_CONSTANT.SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();
        for(GrantedAuthority authrority: authorities){
            auths.add(authrority.getAuthority());
        }
        return String.join(",", auths);
    }

}
