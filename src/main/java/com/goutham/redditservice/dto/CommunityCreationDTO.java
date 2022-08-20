package com.goutham.redditservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityCreationDTO {
    private String communityName;
    private String about;
    private String createdBy;
    private String profilePicUrl;
}
