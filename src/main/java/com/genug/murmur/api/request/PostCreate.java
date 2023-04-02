package com.genug.murmur.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostCreate {

    @NotBlank(message = "{validation.post.title.notBlank}")
    public String title;

    @NotBlank(message = "{validation.post.content.notBlank}")
    public String content;

}
