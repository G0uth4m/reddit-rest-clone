package com.goutham.redditservice.entity;

import com.goutham.redditservice.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "IS_DELETED = false")
@SQLDelete(sql = "UPDATE " + AppConstants.APP_USERS_TABLE + " SET IS_DELETED = true WHERE USER_ID = ?")
@Table(name = AppConstants.APP_USERS_TABLE, catalog = AppConstants.REDDIT_CLONE_SCHEMA)
public class AppUser {

    @Id
    @Column(name = "USER_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "PROFILE_PIC_URL")
    private String profilePicUrl;

    @Column(name = "KARMA")
    private Integer karma;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "communities_members",
            joinColumns = {@JoinColumn(name = "members_user_id")},
            inverseJoinColumns = {@JoinColumn(name = "community_community_id")}
    )
    private Set<Community> communities;

    @Column(name = "IS_DELETED")
    private Boolean isDeleted;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
