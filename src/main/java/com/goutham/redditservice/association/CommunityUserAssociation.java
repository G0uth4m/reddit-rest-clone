package com.goutham.redditservice.association;

import com.goutham.redditservice.constant.AppConstants;
import com.goutham.redditservice.key.CommunityUserAssociationKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = AppConstants.COMMUNITY_USER_MAPPING_TABLE, catalog = AppConstants.REDDIT_CLONE_SCHEMA)
public class CommunityUserAssociation {

    @EmbeddedId
    private CommunityUserAssociationKey communityUserAssociationKey;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
