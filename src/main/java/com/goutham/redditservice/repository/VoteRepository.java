package com.goutham.redditservice.repository;

import com.goutham.redditservice.constant.SqlQueries;
import com.goutham.redditservice.entity.Vote;
import com.goutham.redditservice.key.VoteKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, VoteKey> {

    @Query(SqlQueries.GET_VOTE_COUNT_BY_POST_ID)
    Long getVoteCountByPostId(@Param("postId") Long postId);
}
