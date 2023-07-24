package com.genug.murmur.api.controller;

import com.genug.murmur.api.request.PostCreate;
import com.genug.murmur.api.request.PostSearch;
import com.genug.murmur.api.request.PostUpdate;
import com.genug.murmur.api.response.PostResponse;
import com.genug.murmur.api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

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

    @PatchMapping("/{postId}")
    public ResponseEntity<?> update(@PathVariable Long postId, @RequestBody PostUpdate request) {
        postService.update(postId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return ResponseEntity.ok().build();
    }
}
