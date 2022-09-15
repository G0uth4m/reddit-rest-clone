package com.goutham.redditservice.repository;

import com.goutham.redditservice.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CommunityRepository extends PagingAndSortingRepository<Community, Long> {
    Optional<Community> findByCommunityName(String communityName);
    Boolean existsByCommunityName(String communityName);
}
