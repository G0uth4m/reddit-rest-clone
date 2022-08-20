package com.goutham.redditservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityDTO {
    private Long communityId;
    private String communityName;
    private String about;
    private String createdBy;
    private String profilePicUrl;
}
