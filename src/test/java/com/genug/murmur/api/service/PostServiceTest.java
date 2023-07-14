package com.genug.murmur.api.service;

import com.genug.murmur.api.domain.Post;
import com.genug.murmur.api.repository.PostRepository;
import com.genug.murmur.api.request.PostCreate;
import com.genug.murmur.api.request.PostUpdate;
import com.genug.murmur.api.request.PostSearch;
import com.genug.murmur.api.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

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
        List<Post> requestPosts = IntStream.rangeClosed(1, 30)
                .mapToObj(i -> Post.builder()
                        .title("title-" + i)
                        .content("content-" + i)
                        .build())
                .toList();
        postRepository.saveAll(requestPosts);

        // Pageable pageable = PageRequest.of(0, 5, DESC, "id");
        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .build();
        // when
        List<PostResponse> response = postService.getList(postSearch);

        // then
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(30, postRepository.count());
        assertEquals(10, response.size());
        assertEquals("title-30", response.get(0).getTitle());
        assertEquals("title-21", response.get(9).getTitle());
    }

    @Test
    @DisplayName("글 제목 수정")
    void test5() {
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

        // when
        postService.update(post.getId(), postEdit);

        // then
        Post editedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));
        assertNotNull(editedPost);
        assertEquals(postEdit.getTitle(), editedPost.getTitle());
        assertEquals("content", editedPost.getContent());
    }

    @Test
    @DisplayName("글 내용 수정")
    void test6() {
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

        // when
        postService.update(post.getId(), postEdit);

        // then
        Post editedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));
        assertNotNull(editedPost);
        assertEquals("title", editedPost.getTitle());
        assertEquals(postEdit.getContent(), editedPost.getContent());
    }

    @Test
    @DisplayName("수정 요청한 글 제목이 null 인 경우 기존의 제목으로 저장")
    void test7() {
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

        // when
        postService.update(post.getId(), postEdit);

        // then
        Post editedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));
        assertNotNull(editedPost);
        assertEquals("title", editedPost.getTitle());
        assertEquals(postEdit.getContent(), editedPost.getContent());
    }

}