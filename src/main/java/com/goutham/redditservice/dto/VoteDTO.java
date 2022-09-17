package com.goutham.redditservice.dto;

import com.goutham.redditservice.enums.VoteTypeEnum;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class VoteDTO {

    @NotNull
    private String username;

    @NotNull
    private VoteTypeEnum voteType;
}
