package com.genug.murmur.security;

import io.jsonwebtoken.Jwts;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
public class TokenProvider {

    private final Key secretKey;

    @Builder
    public TokenProvider(Key secretKey) {
        this.secretKey = secretKey;
    }

    public String create(String subject) {
        Date expiryDate = Date.from(Instant.now().plus(20, ChronoUnit.SECONDS));
        return Jwts.builder()
                // header
                .signWith(secretKey)
                // payload
                .setSubject(subject) // sub
                .setIssuer("Murmur app") // iss
                .setIssuedAt(new Date()) // iat
                .setExpiration(expiryDate) // exp
                .compact();
    }

    public String validateAndGetSubject(String jws) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jws)
                .getBody()
                .getSubject();
    }

    public Date getRemainingExpirationDate(String jws) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jws)
                .getBody().getExpiration();
    }
}
