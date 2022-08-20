package com.goutham.redditservice.repository;

import com.goutham.redditservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PostRepository extends PagingAndSortingRepository<Post, Long> {
    Page<Post> findAllByCommunity_CommunityName(String communityName, Pageable pageable);
    Page<Post> findAllByAuthor_Username(String username, Pageable pageable);
}
