package com.example.userservice.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${jwt.token.raw_secret_key}")
    private String rawSecretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value(("${jwt.refresh-token-expiration}"))
    private long refreshTokenExpiration;

    // 서명 용도
    private Key getSecretKey() { return Keys.hmacShaKeyFor(rawSecretKey.getBytes()); }

    // 키 생성 함수
    public String createToken(String email, String role, long expiration) {
        Map<String,Object> claims = new HashMap<>();

        if(email != null) {
            claims.put("email", email);
        }
        if(role != null) {
            claims.put("role", role);
        }

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 엑세스 토큰 생성
    public String createAccessToken(String email, String role) {
        return createToken(email, role, accessTokenExpiration);
    }

    // 리프레시 토큰 생성
    public String createRefreshToken() {
        return createToken(null, null, refreshTokenExpiration);
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
