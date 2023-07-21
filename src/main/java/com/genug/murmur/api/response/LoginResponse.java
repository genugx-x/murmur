package com.genug.murmur.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {
    private Long userId;
    private String token;

    @Builder
    public LoginResponse(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
