package com.genug.murmur.api.controller;

import com.genug.murmur.api.request.PostCreate;
import com.genug.murmur.api.request.PostSearch;
import com.genug.murmur.api.response.PostResponse;
import com.genug.murmur.api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    // Http Method
    // GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD, TRACE, CONNECT

    // 글 등록
    @PostMapping
    public ResponseEntity<?> post(@RequestBody @Valid PostCreate request) {
        Long postId = postService.write(request);
        return ResponseEntity.ok(postId);
    }

    @GetMapping
    public ResponseEntity<?> getList(@ModelAttribute PostSearch request) {
        return ResponseEntity.ok(postService.getList(request));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> get(@PathVariable Long postId) {
        PostResponse response = postService.get(postId);
        return ResponseEntity.ok(response);
    }

}
