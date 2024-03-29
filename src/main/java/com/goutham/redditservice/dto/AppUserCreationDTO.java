package com.goutham.redditservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppUserCreationDTO {
    private String username;
    private String password;
    private String email;
    private String profilePicUrl;
}
