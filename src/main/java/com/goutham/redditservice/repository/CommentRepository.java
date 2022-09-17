package com.goutham.redditservice.repository;

import com.goutham.redditservice.constant.SqlQueries;
import com.goutham.redditservice.dto.CommentDTO;
import com.goutham.redditservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {

    @Query(SqlQueries.GET_COMMENT_DTO_BY_ID)
    Optional<CommentDTO> findByCommentId(@Param("commentId") Long commentId);

    @Query(SqlQueries.GET_COMMENT_DTO_BY_POST_ID)
    Page<CommentDTO> findAllByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query(SqlQueries.GET_COMMENT_DTO_BY_USER_ID)
    Page<CommentDTO> findAllByCommenterId(@Param("userId") Long userId, Pageable pageable);
}
