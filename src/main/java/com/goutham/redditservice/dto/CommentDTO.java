package com.goutham.redditservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentDTO {
    private Long commentId;
    private String content;
    private String author;
    private Long votes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
