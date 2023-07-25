package com.genug.murmur.security;

import com.genug.murmur.api.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = parseBearerToken(request);
            // Blacklist token check
            if (token != null && !token.equalsIgnoreCase("null") && !isBlackList(token)) {
                Long userId = Long.parseLong(tokenProvider.validateAndGetSubject(token));
                var authenticationToken =
                        new UsernamePasswordAuthenticationToken(userId, null, AuthorityUtils.NO_AUTHORITIES);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);
            }
        } catch (Exception e) {
            log.error("JwtAuthenticationFilter.doFilterInternal(..) ex={}", e.toString());

        }
        filterChain.doFilter(request, response);
    }

    /*
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/login");
    }
     */

    private String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authentication");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 인자의 토큰이 redis 에 저장되어 있지 않거나, "logout" 값을 가지고 있다면 블랙리스트이다.
    public boolean isBlackList(String token) {
        String value = redisService.getValue(token);
        boolean result = value != null && value.equals("logout");
        if (result) {
            throw new RuntimeException("잘못된 접근입니다. 로그인 페이지로 이동합니다.");
        }
        return false;
    }
}
