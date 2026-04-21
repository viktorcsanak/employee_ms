package com.example.userservice.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key =
        Keys.hmacShaKeyFor(
            "replace-this-with-a-long-256-bit-secret-key".getBytes()
        );

    public String generateToken(Integer Id) {
        return Jwts.builder()
                .subject(Id.toString())
                .issuedAt(new Date())
                .expiration(
                    new Date(System.currentTimeMillis() + 86400000)
                )
                .signWith(key)
                .compact();
    }

    public Integer verifyToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Integer.parseInt(claims.getSubject()); // returns Id

        } catch (JwtException e) {
            throw new JwtException("Invalid or expired token");
        }
    }
}