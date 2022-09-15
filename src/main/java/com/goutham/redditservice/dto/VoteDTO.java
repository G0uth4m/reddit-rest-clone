package com.goutham.redditservice.dto;

import com.goutham.redditservice.enums.VoteTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteDTO {
    private String username;
    private VoteTypeEnum voteType;
}
