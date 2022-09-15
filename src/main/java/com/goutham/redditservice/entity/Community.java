package com.goutham.redditservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "update " + AppConstants.COMMUNITY_TABLE + " SET is_deleted = true WHERE community_id = ?")
@Table(name = AppConstants.COMMUNITY_TABLE, catalog = AppConstants.REDDIT_CLONE_SCHEMA)
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "community_id", nullable = false)
    private Long communityId;

    @Column(name = "community_name", nullable = false, unique = true)
    private String communityName;

    @Column(name = "about")
    private String about;

    @Column(name = "profile_pic_url")
    private String profilePicUrl;

    @ManyToOne
    private AppUser createdBy;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
