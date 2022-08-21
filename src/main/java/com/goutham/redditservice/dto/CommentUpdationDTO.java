package com.goutham.redditservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentUpdationDTO {
    private String content;
    private String postId;
}
