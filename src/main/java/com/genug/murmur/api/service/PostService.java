package com.genug.murmur.api.service;

import com.genug.murmur.api.domain.Post;
import com.genug.murmur.api.domain.PostEditor;
import com.genug.murmur.api.repository.PostRepository;
import com.genug.murmur.api.request.PostCreate;
import com.genug.murmur.api.request.PostEdit;
import com.genug.murmur.api.request.PostSearch;
import com.genug.murmur.api.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<PostResponse> getList(PostSearch postSearch) {
//        Pageable pageable = PageRequest.of(page, 5, Sort.by("id").descending());
        return postRepository.getList(postSearch).stream()
//                .map(post -> PostResponse.builder()
//                        .id(post.getId())
//                        .title(post.getTitle())
//                        .content(post.getContent())
//                        .build())
                .map(PostResponse::new)
                .toList();
    }

    public PostResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    @Transactional
    public void edit(Long id, PostEdit postEdit) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글 입니다."));
        PostEditor.PostEditorBuilder editorBuilder = post.toEditor();
//        if (postEdit.getTitle() != null) {
//            editorBuilder.title(postEdit.getTitle());
//        }
//        if (postEdit.getContent() != null) {
//            editorBuilder.content(postEdit.getContent());
//        }
//        post.edit(editorBuilder.build());
        post.edit(editorBuilder
                .title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build());
    }
}