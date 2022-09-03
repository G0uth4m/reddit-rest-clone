package com.goutham.redditservice.repository;

import com.goutham.redditservice.association.CommunityUserAssociation;
import com.goutham.redditservice.key.CommunityUserAssociationKey;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CommunityUserAssociationRepository extends PagingAndSortingRepository<CommunityUserAssociation, CommunityUserAssociationKey> {
}
