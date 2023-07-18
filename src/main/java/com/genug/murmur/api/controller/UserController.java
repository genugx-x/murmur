package com.genug.murmur.api.controller;

import com.genug.murmur.api.domain.User;
import com.genug.murmur.api.request.UserLogin;
import com.genug.murmur.api.service.UserService;
import com.genug.murmur.security.TokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLogin userLogin) {
        User user = userService.login(userLogin.getEmail(), userLogin.getPassword());
        tokenProvider.create(String.valueOf(user.getId()));
        return ResponseEntity.ok().build();
    }
}
