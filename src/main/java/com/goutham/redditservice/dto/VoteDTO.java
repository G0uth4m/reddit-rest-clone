package com.goutham.redditservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteDTO {
    private String username;
    private Integer vote;
}
