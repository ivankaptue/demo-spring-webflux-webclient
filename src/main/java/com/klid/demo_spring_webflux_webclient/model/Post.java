package com.klid.demo_spring_webflux_webclient.model;

/**
 * @author Ivan Kaptue
 */
public class Post {

    private Long id;
    private String title;
    private String body;
    private Long userId;

    public Post() {
    }

    public Post(String title, String body, Long userId) {
        this.title = title;
        this.body = body;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
