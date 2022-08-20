package com.goutham.redditservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityUpdationDTO {
    private String about;
    private String profilePicUrl;
}
