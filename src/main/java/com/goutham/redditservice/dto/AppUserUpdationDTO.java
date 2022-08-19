package com.goutham.redditservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppUserUpdationDTO {
    private String email;
    private String profilePicUrl;
}
