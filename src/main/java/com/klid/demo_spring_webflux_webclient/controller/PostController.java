package com.klid.demo_spring_webflux_webclient.controller;

import com.klid.demo_spring_webflux_webclient.model.Post;
import com.klid.demo_spring_webflux_webclient.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Ivan Kaptue
 */
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping("")
    public ResponseEntity<List<Post>> findAllPosts() {
        return ResponseEntity.ok(postService.findAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> findOnePost(@PathVariable(name = "id") Integer id) {
        return ResponseEntity.ok(postService.findOnePost(id));
    }
}
