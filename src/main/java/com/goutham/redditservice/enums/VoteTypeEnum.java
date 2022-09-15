package com.goutham.redditservice.enums;

import com.goutham.redditservice.entity.VoteType;

public enum VoteTypeEnum {

    UPVOTE(VoteType.builder().voteTypeId(1).voteTypeName("upvote").voteTypeValue(1).build()),
    DOWNVOTE(VoteType.builder().voteTypeId(2).voteTypeName("downvote").voteTypeValue(-1).build());

    public final VoteType voteType;

    VoteTypeEnum(VoteType voteType) {
        this.voteType = voteType;
    }

    public VoteType value() {
        return voteType;
    }
}
