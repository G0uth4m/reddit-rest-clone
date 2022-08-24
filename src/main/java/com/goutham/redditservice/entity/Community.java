package com.goutham.redditservice.entity;

import com.goutham.redditservice.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "IS_DELETED = false")
@SQLDelete(sql = "UPDATE " + AppConstants.COMMUNITY_TABLE + " SET IS_DELETED = true WHERE COMMUNITY_ID = ?")
@Table(name = AppConstants.COMMUNITY_TABLE, catalog = AppConstants.REDDIT_CLONE_SCHEMA)
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "COMMUNITY_ID", nullable = false)
    private Long communityId;

    @Column(name = "COMMUNITY_NAME", nullable = false, unique = true)
    private String communityName;

    @Column(name = "ABOUT")
    private String about;

    @Column(name = "PROFILE_PIC_URL")
    private String profilePicUrl;

    @ManyToOne
    private AppUser createdBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "communities_members",
            inverseJoinColumns = {@JoinColumn(name = "members_user_id")},
            joinColumns = {@JoinColumn(name = "community_community_id")}
    )
    private Set<AppUser> members;

    @Column(name = "IS_DELETED")
    private Boolean isDeleted;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
