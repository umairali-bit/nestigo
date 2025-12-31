package com.nestigo.systemdesign.nestigo.security;


import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private SecretKey getJwtSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserEntity  user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().stream().map(Enum::name).toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*10))
                .signWith(getJwtSecretKey())
                .compact();

    }

    public String generateRefreshToken(UserEntity  user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L *60*60*24*30*6))
                .signWith(getJwtSecretKey())
                .compact();
    }

    // verifying claim
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getJwtSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
// if we want to avoid db lookup
    //extracting userId
    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    //extracting email
    public String getEmail(String token) {
        return getClaims(token).get("email").toString();
    }

    //extracting roles and return a list
    public List<String> getRoles(String token) {
        Claims  claims = getClaims(token);
        Object  roles = claims.get("roles");
        if (roles instanceof List<?> list) {
            return (list.stream().map(item -> String.valueOf(item)).toList());
        }
        return List.of();
    }


}







