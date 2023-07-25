package com.genug.murmur.api.controller;

import com.genug.murmur.api.domain.User;
import com.genug.murmur.api.request.Login;
import com.genug.murmur.api.response.LoginResponse;
import com.genug.murmur.api.service.RedisService;
import com.genug.murmur.api.service.UserService;
import com.genug.murmur.security.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final RedisService redisService;
    private final TokenProvider tokenProvider;

    // 임시 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signUp() {
        userService.signUp();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid Login login) {
        User user = userService.login(login.getEmail(), login.getPassword());
        String token = tokenProvider.create(String.valueOf(user.getId()));
        redisService.delete(token);
        return ResponseEntity.ok(LoginResponse.builder()
                .userId(user.getId())
                .token(token)
                .build());
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authentication");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            Date expd = tokenProvider.getRemainingExpirationDate(token);
            long current = System.currentTimeMillis();
            redisService.setValue(token, "logout", expd.getTime()-current);
        }
    }
}
