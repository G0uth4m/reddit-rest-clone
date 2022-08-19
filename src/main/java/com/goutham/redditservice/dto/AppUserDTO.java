package com.goutham.redditservice.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonRootName(value = "user")
public class AppUserDTO {
    private Long userId;
    private String username;
    private String email;
    private String profilePicUrl;
    private Integer karma;
}
