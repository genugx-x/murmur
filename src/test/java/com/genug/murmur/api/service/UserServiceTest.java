package com.genug.murmur.api.service;

import com.genug.murmur.api.domain.User;
import com.genug.murmur.api.exception.LoginFailException;
import com.genug.murmur.api.exception.UserNotFoundException;
import com.genug.murmur.api.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        String encryptedPassword = passwordEncoder.encode("12345");
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
    @DisplayName("로그인 시 정상이어야 한다.")
    void test() {
        // given
        String email = "genug@gmail.com";
        String password = "12345";

        // when
        User user = userService.login(email, password);

        // then
        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertTrue(passwordEncoder.matches(password, user.getPassword()));
    }

    @Test
    @DisplayName("이메일이 없는 경우 로그인 시 UserNotFoundException이 발생한다.")
    void test2() {
        // given
        String email = "xxxx@gmail.com";
        String password = "12345";

        // expected
        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> userService.login(email, password));

        assertEquals(404, e.getStatusCode());
        assertEquals("존재하지 않는 계정입니다.", e.getMessage());
    }

    @Test
    @DisplayName("패스워드가 틀린 경우 로그인 시 LoginFailException을 발생한다.")
    void test3() {
        // given
        String email = "genug@gmail.com";
        String password = "2222";

        // expected
        LoginFailException e = assertThrows(LoginFailException.class,
                () -> userService.login(email, password));

        assertEquals(401, e.getStatusCode());
        assertEquals("로그인에 실패하였습니다.\n" +
                " 아이디 또는 비밀번호 확인 후 다시 시도해주세요.", e.getMessage());
    }

}