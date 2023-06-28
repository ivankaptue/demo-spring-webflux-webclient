package com.klid.demo_spring_webflux_webclient.model;

import lombok.Data;

/**
 * @author Ivan Kaptue
 */
@Data
public class Post {
    private Long id;
    private String title;
    private String body;
    private Long userId;
}
