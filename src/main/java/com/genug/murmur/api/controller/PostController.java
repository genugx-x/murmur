package com.genug.murmur.api.controller;

import com.genug.murmur.api.request.PostCreate;
import com.genug.murmur.api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // Http Method
    // GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD, TRACE, CONNECT

    // 글 등록
    @PostMapping("/posts")
    public ResponseEntity<?> post(@RequestBody @Valid PostCreate request) {
        Long postId = postService.write(request);
        return ResponseEntity.ok(postId);
    }


}
