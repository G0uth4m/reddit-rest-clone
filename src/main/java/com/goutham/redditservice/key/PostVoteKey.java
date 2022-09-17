package com.goutham.redditservice.key;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PostVoteKey implements Serializable {

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "user_id")
    private Long userId;
}
