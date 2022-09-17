package com.goutham.redditservice.repository;

import com.goutham.redditservice.constant.SqlQueries;
import com.goutham.redditservice.entity.PostVote;
import com.goutham.redditservice.key.PostVoteKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostVoteRepository extends JpaRepository<PostVote, PostVoteKey> {

    @Query(SqlQueries.GET_VOTE_COUNT_BY_POST_ID)
    Long getVoteCountByPostId(@Param("postId") Long postId);
}
