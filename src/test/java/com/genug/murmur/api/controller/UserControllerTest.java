package com.genug.murmur.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genug.murmur.api.domain.User;
import com.genug.murmur.api.repository.UserRepository;
import com.genug.murmur.api.request.Login;
import com.genug.murmur.api.response.LoginResponse;
import com.genug.murmur.api.service.AuthService;
import com.genug.murmur.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;


    @BeforeEach
    void setUp() {
        String encryptedPassword = passwordEncoder.encode("12345");
        for (int i = 0; i < 5; i++) {
            userRepository.save(User.builder()
                    .email("xxx" + i + "@gmail.com")
                    .password(encryptedPassword)
                    .nickname("xxxx")
                    .build());
        }
        User user = User.builder()
                .email("genug@gmail.com")
                .password(encryptedPassword)
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
        Login request = Login.builder()
                .email("genug@gmail.com")
                .password("12345")
                .build();
        // expected
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.token").isNotEmpty())
                .andDo(print())
                .andReturn();
        LoginResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
        mockMvc.perform(get("/posts/1")
                        .header("Authentication", "Bearer " + response.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 계정 로그인 시 UserNotFoundException 예외를 응답받는다.")
    void loginWithUserNotFound() throws Exception {
        // given
        Login request = Login.builder()
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
        Login request = Login.builder()
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
        Login request = Login.builder()
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

    @Test
    @DisplayName("비밀번호가 다른 경우 로그인 실패 메시지와 함께 예외를 전달한다.")
    void loginFailTest() throws Exception {
        // given
        Login request = Login.builder()
                .email("genug@gmail.com")
                .password("123456")
                .build();

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isUnauthorized(),
                        jsonPath("$.code").value(401),
                        jsonPath("$.message")
                                .value("로그인에 실패하였습니다.\n 아이디 또는 비밀번호 확인 후 다시 시도해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 후 클라이언트가 기존 JWT로 글 작성을 시도 한다면 403 예외가 발생해야 한다.")
    void logoutTest() throws Exception {
        // given
        Login request = Login.builder()
                .email("genug@gmail.com")
                .password("12345")
                .build();

        //when 1 - Login
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.token").isNotEmpty())
                .andReturn();
        //when 2 - Logout
        LoginResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
        mockMvc.perform(get("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authentication", "Bearer " + response.getToken()))
                .andExpectAll(
                        status().isOk());

        // expected
        mockMvc.perform(get("/posts/1")
                        .header("Authentication", "Bearer " + response.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isForbidden())
                .andDo(print());
        assertEquals("logout", authService.getValue(response.getToken()));
    }

    @Test
    void test() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwiaXNzIjoiTXVybXVyIGFwcCIsImlhdCI6MTY4OTkyNzYwNywiZXhwIjoxNjg5OTI3NjY3fQ.KUb1GVQ-LU9qgQn2yGp-3u6S-m2dvI3h_6dic6F8oks";
        // expected
        mockMvc.perform(get("/posts/1")
                        .header("Authentication", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk())
                .andDo(print());
    }
}