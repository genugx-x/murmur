package com.genug.murmur.config;

import com.genug.murmur.security.TokenProvider;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.security.Key;

@Configuration
public class SecurityConfig {

    @Bean
    public TokenProvider tokenProvider() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return TokenProvider.builder()
                .secretKey(Keys.secretKeyFor(SignatureAlgorithm.HS256))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new SCryptPasswordEncoder(
                16,
                8,
                1,
                32,
                64);
    }
}
