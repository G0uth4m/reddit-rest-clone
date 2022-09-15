package com.goutham.redditservice.repository;

import com.goutham.redditservice.constant.SqlQueries;
import com.goutham.redditservice.dto.PostDTO;
import com.goutham.redditservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends PagingAndSortingRepository<Post, Long> {
    Page<Post> findAllByCommunity_CommunityName(String communityName, Pageable pageable);
    Page<Post> findAllByAuthor_Username(String username, Pageable pageable);

    @Query(SqlQueries.GET_POST_DTO_BY_ID)
    Optional<PostDTO> findPostDTOById(@Param("postId") Long postId);

    @Query(SqlQueries.GET_ALL_POST_DTO_BY_COMMUNITY_ID)
    Page<PostDTO> findAllPostDTOByCommunityId(@Param("communityId") Long communityId, Pageable pageable);

    @Query(SqlQueries.GET_ALL_POST_DTO_BY_USER_ID)
    Page<PostDTO> findAllPostDTOByUserId(@Param("userId") Long userId, Pageable pageable);
}
