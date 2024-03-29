package com.goutham.redditservice.entity;

import com.goutham.redditservice.constant.AppConstants;
import com.goutham.redditservice.key.PostVoteKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = AppConstants.POST_VOTE_TABLE, catalog = AppConstants.REDDIT_CLONE_SCHEMA)
public class PostVote {

    @EmbeddedId
    private PostVoteKey postVoteKey;

    @ManyToOne
    @JoinColumn(name = "vote_type_id")
    private VoteType voteType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
