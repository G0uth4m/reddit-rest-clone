package com.goutham.redditservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityJoinDTO {
    private String username;
    private String updatedAt;
}
