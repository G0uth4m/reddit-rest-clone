package com.goutham.redditservice.service;

import com.goutham.redditservice.dto.AppUserDTO;
import com.goutham.redditservice.dto.CommunityCreationDTO;
import com.goutham.redditservice.dto.CommunityDTO;
import com.goutham.redditservice.dto.CommunityJoinDTO;
import com.goutham.redditservice.dto.CommunityUpdationDTO;
import com.goutham.redditservice.entity.AppUser;
import com.goutham.redditservice.entity.Community;
import com.goutham.redditservice.exception.AppUserAlreadyExistsException;
import com.goutham.redditservice.exception.AppUserNotFoundException;
import com.goutham.redditservice.exception.CommunityAlreadyExistsException;
import com.goutham.redditservice.exception.CommunityNotFoundException;
import com.goutham.redditservice.repository.CommunityRepository;
import com.goutham.redditservice.repository.AppUserRepository;
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
    private AppUserRepository appUserRepository;

    public CommunityDTO createCommunity(CommunityCreationDTO communityCreationDTO) {
        AppUser appUser = appUserRepository.findByUsername(communityCreationDTO.getCreatedBy()).orElseThrow(() -> {
            log.error("User: {} does not exist", communityCreationDTO.getCreatedBy());
            return new AppUserNotFoundException("User does not exist");
        });

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
                .members(Collections.emptySet())
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
        Community community = communityRepository.findByCommunityName(communityName).orElseThrow(() -> {
            log.error("Community: {} does not exist", communityName);
            return new CommunityNotFoundException("Community does not exist");
        });
        community.setAbout(communityUpdationDTO.getAbout());
        community.setProfilePicUrl(communityUpdationDTO.getProfilePicUrl());
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
        Community community = communityRepository.findByCommunityName(communityName).orElseThrow(() -> {
            log.error("Community: {} does not exist", communityName);
            return new CommunityNotFoundException("Community does not exist");
        });

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
        Community community = communityRepository.findByCommunityName(communityName).orElseThrow(() -> {
            log.error("Community: {} does not exist", communityName);
            return new AppUserNotFoundException("Community does not exist");
        });
        communityRepository.deleteById(community.getCommunityId());
    }

    public List<AppUserDTO> getCommunityMembers(String communityName) {
        Community community = communityRepository.findByCommunityName(communityName).orElseThrow(() -> {
            log.error("Community: {} does not exist", communityName);
            return new CommunityNotFoundException("Community does not exist");
        });
        return community.getMembers().stream()
                .map(appUser -> AppUserDTO.builder()
                        .userId(appUser.getUserId())
                        .username(appUser.getUsername())
                        .email(appUser.getEmail())
                        .profilePicUrl(appUser.getProfilePicUrl())
                        .karma(appUser.getKarma())
                        .build())
                .collect(Collectors.toList());
    }

    public void addMemberToCommunity(String communityName, CommunityJoinDTO communityJoinDTO) {
        AppUser appUser = appUserRepository.findByUsername(communityJoinDTO.getUsername()).orElseThrow(() -> {
            log.error("User: {} does not exist", communityJoinDTO.getUsername());
            return new AppUserNotFoundException("User does not exist");
        });

        Community community = communityRepository.findByCommunityName(communityName).orElseThrow(() -> {
            log.error("Community: {} does not exist", communityName);
            return new CommunityNotFoundException("Community does not exist");
        });

        if (community.getMembers().contains(appUser)) {
            log.error("User: {} already part of community: {}", appUser.getUsername(), community.getCommunityName());
            throw new AppUserAlreadyExistsException("User already part of community");
        }

        community.getMembers().add(appUser);
        communityRepository.save(community);
    }

    public void removeMemberFromCommunity(String communityName, String username) {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User: {} does not exist", username);
            return new AppUserNotFoundException("User does not exist");
        });

        Community community = communityRepository.findByCommunityName(communityName).orElseThrow(() -> {
            log.error("Community: {} does not exist", communityName);
            return new CommunityNotFoundException("Community does not exist");
        });

        if (!community.getMembers().contains(appUser)) {
            log.error("User: {} not part of community: {}", appUser.getUsername(), community.getCommunityName());
            throw new AppUserAlreadyExistsException("User not part of community");
        }

        community.getMembers().remove(appUser);
        communityRepository.save(community);
    }
}
