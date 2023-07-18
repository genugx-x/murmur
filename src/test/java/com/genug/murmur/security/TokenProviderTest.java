package com.genug.murmur.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("JWT 생성 및 검증 정상")
    void createAndValidateTest() {
        String subject = "subject123123"; // given
        String jws = tokenProvider.create(subject); // when
        assertEquals(subject, tokenProvider.validateAndGetSubject(jws)); // then
    }
}