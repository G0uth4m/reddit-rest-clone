package com.goutham.redditservice.repository;

import com.goutham.redditservice.association.CommunityUserAssociation;
import com.goutham.redditservice.constant.SqlQueries;
import com.goutham.redditservice.entity.AppUser;
import com.goutham.redditservice.entity.Community;
import com.goutham.redditservice.key.CommunityUserAssociationKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface CommunityUserAssociationRepository extends PagingAndSortingRepository<CommunityUserAssociation, CommunityUserAssociationKey> {

    @Query(SqlQueries.FIND_ALL_MEMBERS_OF_COMMUNITY)
    Page<AppUser> findAllByCommunityId(@Param("communityId") Long communityId, Pageable pageable);

    @Query(SqlQueries.FIND_ALL_COMMUNITIES_OF_USER)
    Page<Community> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}
