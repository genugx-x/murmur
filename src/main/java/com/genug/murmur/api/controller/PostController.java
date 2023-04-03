package com.genug.murmur.api.controller;

import com.genug.murmur.api.request.PostCreate;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class PostController {

    // Http Method
    // GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD, TRACE, CONNECT

    // 글 등록
    @PostMapping("/posts")
    public Map<String, String> post(@RequestBody @Valid PostCreate postCreate) {
        return Map.of();
    }


}
