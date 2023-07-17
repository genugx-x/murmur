package com.genug.murmur.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genug.murmur.api.domain.User;
import com.genug.murmur.api.repository.UserRepository;
import com.genug.murmur.api.request.UserLogin;
import com.genug.murmur.api.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("genug@gmail.com")
                .password("12345")
                .nickname("genugxx")
                .build();
        userRepository.save(user);
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccessTest() throws Exception {
        // given
        UserLogin request = UserLogin.builder()
                .email("genug@gmail.com")
                .password("12345")
                .build();

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("존재하지 않는 계정 로그인 시 UserNotFoundException 예외를 응답받는다.")
    void loginWithUserNotFound() throws Exception {
        // given
        UserLogin request = UserLogin.builder()
                .email("genug123@gmail.com")
                .password("12345")
                .build();

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.code").value("404"),
                        jsonPath("$.message").value("존재하지 않는 계정입니다.")
                )
                .andDo(print());

    }

    @Test
    @DisplayName("이메일을 입력하지 않은 상태로 로그인 요청 시 '이메일을 입력하세요.' 메시지 응답")
    void loginWithBlankEmailTest() throws Exception {
        // given
        UserLogin request = UserLogin.builder()
                .email("")
                .password("12345")
                .build();

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.validation.email")
                                .value("이메일을 입력하세요."))
                .andDo(print());

    }

    @Test
    @DisplayName("패스워드를 입력하지 않은 상태로 로그인 요청 시 '비밀번호를 입력하세요.' 메시지 응답")
    void loginWithBlankPasswordTest() throws Exception {
        // given
        UserLogin request = UserLogin.builder()
                .email("gggg@gmail.com")
                .password("")
                .build();

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.validation.password")
                                .value("비밀번호를 입력하세요."))
                .andDo(print());
    }
}