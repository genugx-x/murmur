package com.genug.murmur.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genug.murmur.api.domain.Post;
import com.genug.murmur.api.domain.User;
import com.genug.murmur.api.repository.PostRepository;
import com.genug.murmur.api.repository.UserRepository;
import com.genug.murmur.api.request.PostCreate;
import com.genug.murmur.api.request.PostUpdate;
import com.genug.murmur.security.TokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenProvider tokenProvider;

    private String token;

    @BeforeEach
    void setUp() {
        String encryptedPassword = passwordEncoder.encode("qA^12345");
        User user = User.builder()
                .email("genug@gmail.com")
                .password(encryptedPassword)
                .nickname("genugxx")
                .build();
        userRepository.save(user);
        token = tokenProvider.create(String.valueOf(user.getId()));
    }

    @AfterEach
    void cleanUp() {
        token = null;
        userRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /posts 요청 시 request를 저장하고 반환값이 없다.")
    void test() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected - MockMvcRequestBuilders.get() .post()
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header("Authentication", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 title 입력은 필수다. ")
    void test2() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header("Authentication", "Bearer " + token)
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
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header("Authentication", "Bearer " + token))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("잘못된 요청입니다."),
                        jsonPath("$.validation.content").value("내용을 입력해주세요.")
                )
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 DB에 1개의 글이 저장되고 그 갯수는 1개이다.")
    void test4() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header("Authentication", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andDo(print());
        // then
        assertEquals(1L, postRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test5() throws Exception {
        // given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .header("Authentication", "Bearer " + token))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(post.getId()),
                        jsonPath("$.title").value("foo"),
                        jsonPath("$.content").value("bar"))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 DB에 1개의 글이 저장되고 그 갯수는 1개이다.")
    void test6() throws Exception {
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header("Authentication", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print());
        // then
        assertEquals(1L, postRepository.count());
    }

    @Test
    @DisplayName("1번 페이지 조회 시 마지막 데이터가 0번 인덱스의 데이터로 조회되어야한다.")
    void test7() throws Exception {
        // given
        List<Post> requestPosts = IntStream.rangeClosed(1, 30)
                .mapToObj(i -> Post.builder()
                        .title("title-" + i)
                        .content("content-" + i)
                        .build())
                .toList();
        postRepository.saveAll(requestPosts);

        // expected
        mockMvc.perform(get("/posts?page=1&size=10")
                        .contentType(APPLICATION_JSON)
                        .header("Authentication", "Bearer " + token))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()", is(10)),
                        jsonPath("$[0].id").value(requestPosts.get(requestPosts.size() - 1).getId()),
                        jsonPath("$[0].title").value(requestPosts.get(requestPosts.size() - 1).getTitle()),
                        jsonPath("$[0].content").value(requestPosts.get(requestPosts.size() - 1).getContent()))
                .andDo(print());

    }

    @Test
    @DisplayName("0번 페이지 조회 시에도 1번 페이지로 조회 되어야 한다.")
    void test8() throws Exception {
        // given
        List<Post> requestPosts = IntStream.rangeClosed(1, 30)
                .mapToObj(i -> Post.builder()
                        .title("title-" + i)
                        .content("content-" + i)
                        .build())
                .toList();
        postRepository.saveAll(requestPosts);

        // expected
        // mockMvc.perform(get("/posts?page=1&sort=id,desc")
        mockMvc.perform(get("/posts?page=0&size=10")
                        .contentType(APPLICATION_JSON)
                        .header("Authentication", "Bearer " + token))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()", is(10)),
                        jsonPath("$[0].id").value(requestPosts.get(requestPosts.size() - 1).getId()),
                        jsonPath("$[0].title").value(requestPosts.get(requestPosts.size() - 1).getTitle()),
                        jsonPath("$[0].content").value(requestPosts.get(requestPosts.size() - 1).getContent()))
                .andDo(print());

    }

    @Test
    @DisplayName("글 제목 수정")
    void test9() throws Exception {
        // given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();
        postRepository.save(post);

        PostUpdate postEdit = PostUpdate.builder()
                .title("Edited title!")
                .content("content")
                .build();

        //expected
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                        .header("Authentication", "Bearer " + token))
                .andExpectAll(
                        status().isOk())
                .andDo(print());
        /*
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(post.getId()),
                        jsonPath("$.title").value("Edited title!"),
                        jsonPath("$.content").value("content"))
                .andDo(print());
         */
    }

    @Test
    @DisplayName("글 내용 수정")
    void test10() throws Exception {
        // given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();
        postRepository.save(post);

        PostUpdate postEdit = PostUpdate.builder()
                .title("title")
                .content("Edited content!")
                .build();

        //expected
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                        .header("Authentication", "Bearer " + token))
                .andExpectAll(
                        status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("수정 요청한 글 제목이 null 인 경우 기존의 제목으로 저장")
    void test11() throws Exception {
        // given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();
        postRepository.save(post);

        PostUpdate postEdit = PostUpdate.builder()
                .title(null)
                .content("Edited content!")
                .build();

        //expected
        // 수정 요청
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                        .header("Authentication", "Bearer " + token))
                .andExpectAll(
                        status().isOk())
                .andDo(print());
        // 수정 내용 확인
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .header("Authentication", "Bearer " + token))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(post.getId()),
                        jsonPath("$.title").value("title"),
                        jsonPath("$.content").value("Edited content!"))
                .andDo(print());

    }

    @Test
    @DisplayName("글 삭제")
    void test12() throws Exception {
        // given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();
        postRepository.save(post);

        //expected
        mockMvc.perform(delete("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .header("Authentication", "Bearer " + token))
                .andExpectAll(
                        status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("존재하지 않는 글 조회")
    void test13() throws Exception {
        Long postId = 999L;
        //expected
        mockMvc.perform(get("/posts/{postId}", postId)
                        .header("Authentication", "Bearer " + token)
                        .contentType(APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.code").value("404"),
                        jsonPath("$.message").value("존재하지 않는 글입니다."),
                        jsonPath("$.validation").isEmpty())
                .andDo(print());

    }

    @Test
    @DisplayName("존재하지 않는 글 수정 요청")
    void test14() throws Exception {
        Long postId = 999L;
        PostUpdate postEdit = PostUpdate.builder()
                .title("title")
                .content("content")
                .build();

        //expected
        mockMvc.perform(patch("/posts/{postId}", postId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                        .header("Authentication", "Bearer " + token))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.code").value("404"),
                        jsonPath("$.message").value("존재하지 않는 글입니다."),
                        jsonPath("$.validation").isEmpty())
                .andDo(print());

    }

    @Test
    @DisplayName("존재하지 않는 글 삭제 요청")
    void test15() throws Exception {
        Long postId = 999L;

        //expected
        mockMvc.perform(delete("/posts/{postId}", postId)
                        .contentType(APPLICATION_JSON)
                        .header("Authentication", "Bearer " + token))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.code").value("404"),
                        jsonPath("$.message").value("존재하지 않는 글입니다."),
                        jsonPath("$.validation").isEmpty())
                .andDo(print());

    }

}