package com.genug.murmur.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostEdit {

    // @NotBlank(message = "타이틀을 입력하세요.")
    @NotBlank(message = "{validation.post.title.notBlank}")
    public String title;

    // @NotBlank(message = "내용을 입력하세요.")
    @NotBlank(message = "{validation.post.content.notBlank}")
    public String content;

    @Builder
    public PostEdit(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
