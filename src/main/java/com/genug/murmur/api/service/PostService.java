package com.genug.murmur.api.service;

import com.genug.murmur.api.domain.Post;
import com.genug.murmur.api.repository.PostRepository;
import com.genug.murmur.api.request.PostCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository; // 생성자 주입

    public Long write(PostCreate postCreate) {
        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();
        postRepository.save(post);
        return post.getId();
    }
}
