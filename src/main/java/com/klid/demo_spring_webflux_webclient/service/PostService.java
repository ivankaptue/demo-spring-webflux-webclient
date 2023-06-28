package com.klid.demo_spring_webflux_webclient.service;

import com.klid.demo_spring_webflux_webclient.model.Post;
import com.klid.demo_spring_webflux_webclient.service.rest.PostRestClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ivan Kaptue
 */
@Service
public class PostService {

    private final PostRestClient postRestClient;

    public PostService(PostRestClient postRestClient) {
        this.postRestClient = postRestClient;
    }

    public List<Post> findAllPosts() {
        return postRestClient.getAllPosts();
    }

    public Post findOnePost(int id) {
        return postRestClient.getOnePost(id).orElseThrow();
    }
}
