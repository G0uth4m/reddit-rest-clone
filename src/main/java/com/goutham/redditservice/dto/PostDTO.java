package com.goutham.redditservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostDTO {
    private Long postId;
    private String title;
    private String content;
    private String author;
    private String community;
}
