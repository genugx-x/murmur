package com.genug.murmur.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class PostControllerTest {

    @Autowired // @WebMvcTest 를 테스트클래스에 추가해줘야함
    private MockMvc mockMvc;

    @Test
    @DisplayName("/posts 요청시 Hello World를 출력한다.")
    void test() throws Exception {
        // expected - MockMvcRequestBuilders.get() .post()
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"궁시렁궁시렁...\", \"content\":\"어쩌구저쩌구\"}")
                )
                .andExpectAll(
                        status().isOk(),
                        content().string("{}")
                )
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 title 입력은 필수다. ")
    void test2() throws Exception {
        // expected - MockMvcRequestBuilders.get() .post()
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"\", \"content\":\"어쩌구저쩌구\"}")
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.title").value("제목을 입력해주세요.")
                )
                .andDo(print());

    }

    @Test
    @DisplayName("/posts 요청시 content 입력은 필수다. ")
    void test3() throws Exception {
        // expected - MockMvcRequestBuilders.get() .post()
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"궁시렁궁시렁...\", \"content\":\"\"}")
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value("내용을 입력해주세요.")
                )
                .andDo(print());
    }
}