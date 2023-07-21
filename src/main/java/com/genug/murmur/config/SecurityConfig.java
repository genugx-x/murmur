package com.genug.murmur.config;

import com.genug.murmur.api.service.AuthService;
import com.genug.murmur.security.JwtAuthenticationFilter;
import com.genug.murmur.security.TokenProvider;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import java.security.Key;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    public final AuthService authService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors() // WebMvcConfig에서 이미 설정했으므로 기본 cors 설정
                .and()
                .csrf().disable() // csrf는 현재 사용하지 않으므로 disable
                .httpBasic().disable() // token을 사용하므로 basic 인증 disable
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // STATELESS : 무상태
                .and()
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    authorizationManagerRequestMatcherRegistry
                            .requestMatchers("/", "/auth/**").permitAll()
                            .anyRequest().authenticated();
                })
                // filter 등록. 매 요청마다 CorsFilter 실행한 후에 jwtAuthenticationFilter 실행한다.
                .addFilterAfter(new JwtAuthenticationFilter(tokenProvider(), authService), CorsFilter.class);
        http.logout().logoutUrl("/logout");
        return http.build();
    }

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
