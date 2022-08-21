package com.goutham.redditservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentCreationDTO {
    private String content;
    private String author;
}
