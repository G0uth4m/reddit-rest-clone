package com.goutham.redditservice.service;

import com.goutham.redditservice.association.CommunityUserAssociation;
import com.goutham.redditservice.dto.AppUserDTO;
import com.goutham.redditservice.dto.CommunityCreationDTO;
import com.goutham.redditservice.dto.CommunityDTO;
import com.goutham.redditservice.dto.CommunityJoinDTO;
import com.goutham.redditservice.dto.CommunityUpdationDTO;
import com.goutham.redditservice.dto.PostDTO;
import com.goutham.redditservice.entity.AppUser;
import com.goutham.redditservice.entity.Community;
import com.goutham.redditservice.entity.Post;
import com.goutham.redditservice.exception.AppUserAlreadyExistsException;
import com.goutham.redditservice.exception.AppUserNotMemberOfCommunityException;
import com.goutham.redditservice.exception.CommunityAlreadyExistsException;
import com.goutham.redditservice.exception.CommunityNotFoundException;
import com.goutham.redditservice.key.CommunityUserAssociationKey;
import com.goutham.redditservice.repository.AppUserRepository;
import com.goutham.redditservice.repository.CommunityRepository;
import com.goutham.redditservice.repository.CommunityUserAssociationRepository;
import com.goutham.redditservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CommunityUserAssociationRepository communityUserAssociationRepository;

    public CommunityDTO createCommunity(CommunityCreationDTO communityCreationDTO) {
        AppUser appUser = appUserService.getUserDAO(communityCreationDTO.getCreatedBy());
        if (communityRepository.existsByCommunityName(communityCreationDTO.getCommunityName())) {
            log.error("Community: {} already exists", communityCreationDTO.getCommunityName());
            throw new CommunityAlreadyExistsException("Community already exists");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        Community community = Community.builder()
                .communityName(communityCreationDTO.getCommunityName())
                .about(communityCreationDTO.getAbout())
                .profilePicUrl(communityCreationDTO.getProfilePicUrl())
                .createdBy(appUser)
                .members(Collections.singleton(appUser))
                .isDeleted(false)
                .createdAt(currentDateTime)
                .updatedAt(currentDateTime)
                .build();

        Community savedCommunity = communityRepository.save(community);

        return CommunityDTO.builder()
                .communityId(savedCommunity.getCommunityId())
                .communityName(savedCommunity.getCommunityName())
                .about(savedCommunity.getAbout())
                .profilePicUrl(savedCommunity.getProfilePicUrl())
                .createdBy(savedCommunity.getCreatedBy().getUsername())
                .build();
    }

    public CommunityDTO updateCommunity(String communityName, CommunityUpdationDTO communityUpdationDTO) {
        Community community = getCommunityDAO(communityName);
        community.setAbout(communityUpdationDTO.getAbout());
        community.setProfilePicUrl(communityUpdationDTO.getProfilePicUrl());
        community.setUpdatedAt(LocalDateTime.now());
        Community updatedCommunity = communityRepository.save(community);

        return CommunityDTO.builder()
                .communityId(updatedCommunity.getCommunityId())
                .communityName(updatedCommunity.getCommunityName())
                .about(updatedCommunity.getAbout())
                .profilePicUrl(updatedCommunity.getProfilePicUrl())
                .createdBy(updatedCommunity.getCreatedBy().getUsername())
                .build();
    }

    public CommunityDTO getCommunity(String communityName) {
        Community community = getCommunityDAO(communityName);
        return CommunityDTO.builder()
                .communityId(community.getCommunityId())
                .communityName(community.getCommunityName())
                .about(community.getAbout())
                .profilePicUrl(community.getProfilePicUrl())
                .createdBy(community.getCreatedBy().getUsername())
                .build();
    }

    public List<CommunityDTO> getCommunities(Pageable pageable) {
        Page<Community> communityPage = communityRepository.findAll(pageable);
        return communityPage.stream()
                .map(community -> CommunityDTO.builder()
                        .communityId(community.getCommunityId())
                        .communityName(community.getCommunityName())
                        .about(community.getAbout())
                        .profilePicUrl(community.getProfilePicUrl())
                        .createdBy(community.getCreatedBy().getUsername())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteCommunity(String communityName) {
        Community community = getCommunityDAO(communityName);
        communityRepository.deleteById(community.getCommunityId());
    }

    public void addMemberToCommunity(String communityName, CommunityJoinDTO communityJoinDTO) {
        AppUser appUser = appUserService.getUserDAO(communityJoinDTO.getUsername());
        Community community = getCommunityDAO(communityName);

        CommunityUserAssociationKey communityUserAssociationKey = CommunityUserAssociationKey.builder()
                .userId(appUser.getUserId())
                .communityId(community.getCommunityId())
                .build();
        if (communityUserAssociationRepository.existsById(communityUserAssociationKey)) {
            log.error("User: {} already part of community: {}", communityJoinDTO.getUsername(), communityName);
            throw new AppUserAlreadyExistsException("User already part of community");
        }

        CommunityUserAssociation communityUserAssociation = CommunityUserAssociation.builder()
                .communityUserAssociationKey(communityUserAssociationKey)
                .createdAt(LocalDateTime.now())
                .build();
        communityUserAssociationRepository.save(communityUserAssociation);
    }

    public void removeMemberFromCommunity(String communityName, String username) {
        AppUser appUser = appUserService.getUserDAO(username);
        Community community = getCommunityDAO(communityName);

        CommunityUserAssociationKey communityUserAssociationKey = CommunityUserAssociationKey.builder()
                .userId(appUser.getUserId())
                .communityId(community.getCommunityId())
                .build();
        if (!communityUserAssociationRepository.existsById(communityUserAssociationKey)) {
            log.error("User: {} not part of community: {}", username, communityName);
            throw new AppUserNotMemberOfCommunityException("User not part of community");
        }
        communityUserAssociationRepository.deleteById(communityUserAssociationKey);
    }

    public List<PostDTO> getCommunityPosts(String communityName, Pageable pageable) {
        Page<Post> postsPage = postRepository.findAllByCommunity_CommunityName(communityName, pageable);
        return postsPage.stream()
                .map(post -> PostDTO.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .author(post.getAuthor().getUsername())
                        .content(post.getContent())
                        .community(post.getCommunity().getCommunityName())
                        .votes((long) (post.getUpvotes().size() - post.getDownvotes().size()))
                        .createdAt(post.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public Community getCommunityDAO(String communityName) {
        return communityRepository.findByCommunityName(communityName).orElseThrow(() -> {
            log.error("Community: {} does not exist", communityName);
            return new CommunityNotFoundException("Community does not exist");
        });
    }

    public List<AppUserDTO> getCommunityMembers(String communityName, Pageable pageable) {
        if (!communityRepository.existsByCommunityName(communityName)) {
            log.error("Community: {} does not exist", communityName);
            throw new CommunityNotFoundException("Community does not exist");
        }

        Page<AppUser> membersPage = appUserRepository.findAllByCommunities_CommunityName(communityName, pageable);
        return membersPage.stream()
                .map(appUser -> AppUserDTO.builder()
                        .userId(appUser.getUserId())
                        .username(appUser.getUsername())
                        .email(appUser.getEmail())
                        .profilePicUrl(appUser.getProfilePicUrl())
                        .karma(appUser.getKarma())
                        .build())
                .collect(Collectors.toList());
    }
}
