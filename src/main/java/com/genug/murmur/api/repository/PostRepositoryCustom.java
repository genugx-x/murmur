package com.genug.murmur.api.repository;

import com.genug.murmur.api.domain.Post;
import com.genug.murmur.api.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> getList(PostSearch postSearch);
}
