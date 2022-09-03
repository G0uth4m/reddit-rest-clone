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
public class CommunityUserAssociationKey implements Serializable {

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "community_id", nullable = false, updatable = false)
    private Long communityId;
}
