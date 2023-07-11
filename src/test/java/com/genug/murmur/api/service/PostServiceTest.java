package com.genug.murmur.api.service;

import com.genug.murmur.api.domain.Post;
import com.genug.murmur.api.repository.PostRepository;
import com.genug.murmur.api.request.PostCreate;
import com.genug.murmur.api.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void test1() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        // when
        postService.write(postCreate);

        // then
        assertEquals(1L, postRepository.count());

        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test2() {
        // given
        Post requestNewPost = Post.builder()
                .title("title")
                .content("content")
                .build();
        postRepository.save(requestNewPost);

        // when
        PostResponse response = postService.get(requestNewPost.getId());

        // then
        assertNotNull(response);
        assertEquals(1, postRepository.count());
        assertEquals("title", response.getTitle());
    }

    @Test
    @DisplayName("글 1개 조회 및 조회된 글은 제목 최대 10글자만 가져와야한다.")
    void test3() {
        // given
        Post requestNewPost = Post.builder()
                .title("123456789012345")
                .content("content")
                .build();
        postRepository.save(requestNewPost);

        // when
        PostResponse response = postService.get(requestNewPost.getId());

        // then
        assertNotNull(response);
        assertEquals(1, postRepository.count());
        assertEquals("1234567890", response.getTitle());
    }

    @Test
    @DisplayName("글 전체 조회")
    void test4() {
        // given
        postRepository.saveAll(List.of(
                Post.builder()
                        .title("제목1")
                        .content("내용내용")
                        .build(),
                Post.builder()
                        .title("제목2")
                        .content("내용내용")
                        .build()
                ));

        // when
        List<PostResponse> response = postService.getList();

        // then
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(2, postRepository.count());
        assertEquals("제목1", response.get(0).getTitle());
        assertEquals("제목2", response.get(1).getTitle());
    }
}
