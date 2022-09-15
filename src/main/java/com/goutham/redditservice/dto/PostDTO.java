package com.goutham.redditservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PostDTO {
    private Long postId;
    private String title;
    private String content;
    private String author;
    private String community;
    private Long votes;
    private LocalDateTime createdAt;
}
