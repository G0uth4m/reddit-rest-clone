package com.goutham.redditservice.entity;

import com.goutham.redditservice.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = AppConstants.VOTE_TYPE_TABLE, catalog = AppConstants.REDDIT_CLONE_SCHEMA)
public class VoteType {

    @Id
    @Column(name = "vote_type_id")
    private Integer voteTypeId;

    @Column(name = "vote_type_name")
    private String voteTypeName;

    @Column(name = "vote_type_value")
    private Integer voteTypeValue;
}
