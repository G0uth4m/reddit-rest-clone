package com.goutham.redditservice.repository;

import com.goutham.redditservice.constant.SqlQueries;
import com.goutham.redditservice.entity.CommentVote;
import com.goutham.redditservice.key.CommentVoteKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentVoteRepository extends JpaRepository<CommentVote, CommentVoteKey> {

    @Query(SqlQueries.GET_VOTE_COUNT_BY_COMMENT_ID)
    Long getVoteCountByCommentId(@Param("commentId") Long commentId);
}
