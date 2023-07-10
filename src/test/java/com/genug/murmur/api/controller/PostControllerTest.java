package com.genug.murmur.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genug.murmur.api.repository.PostRepository;
import com.genug.murmur.api.request.PostCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired // @AutoConfigureMockMvc를 테스트클래스에 추가해줘야함
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach // 각각의 테스트 메서드가 수행되기 전에 수행됨.
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/posts 요청시 Hello World를 출력한다.")
    void test() throws Exception {
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);
        // System.out.println(json);

        // expected - MockMvcRequestBuilders.get() .post()
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpectAll(
                        status().isOk(),
                        content().string("1")
                )
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 title 입력은 필수다. ")
    void test2() throws Exception {
        PostCreate request = PostCreate.builder()
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("잘못된 요청입니다."),
                        jsonPath("$.validation.title").value("제목을 입력해주세요.")
                )
                .andDo(print());

    }

    @Test
    @DisplayName("/posts 요청시 content 입력은 필수다. ")
    void test3() throws Exception {
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("잘못된 요청입니다."),
                        jsonPath("$.validation.content").value("내용을 입력해주세요.")
                )
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 DB에 값이 저장된다.")
    void test4() throws Exception {
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpectAll(
                        status().isOk(),
                        content().string("1")
                )
                .andDo(print());
        // then
        assertEquals(1L, postRepository.count());
    }

}