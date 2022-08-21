package com.goutham.redditservice.repository;

import com.goutham.redditservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {
    Page<Comment> findAllByPost_PostId(Long postId, Pageable pageable);
}
