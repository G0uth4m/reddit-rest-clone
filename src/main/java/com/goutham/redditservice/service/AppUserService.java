package com.goutham.redditservice.service;

import com.goutham.redditservice.constant.AppConstants;
import com.goutham.redditservice.dto.AppUserCreationDTO;
import com.goutham.redditservice.dto.AppUserDTO;
import com.goutham.redditservice.dto.AppUserUpdationDTO;
import com.goutham.redditservice.dto.PostDTO;
import com.goutham.redditservice.entity.AppUser;
import com.goutham.redditservice.entity.Post;
import com.goutham.redditservice.exception.AppUserAlreadyExistsException;
import com.goutham.redditservice.exception.AppUserNotFoundException;
import com.goutham.redditservice.repository.AppUserRepository;
import com.goutham.redditservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PostRepository postRepository;

    public AppUserDTO createAppUser(AppUserCreationDTO appUserCreationDTO) {
        if (appUserRepository.existsByUsername(appUserCreationDTO.getUsername())) {
            log.error("User: {} already exist", appUserCreationDTO.getUsername());
            throw new AppUserAlreadyExistsException("User already exists");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        AppUser appUser = AppUser.builder()
                .username(appUserCreationDTO.getUsername())
                .password(appUserCreationDTO.getPassword())
                .email(appUserCreationDTO.getEmail())
                .profilePicUrl(appUserCreationDTO.getProfilePicUrl())
                .karma(0)
                .isDeleted(false)
                .createdAt(currentDateTime)
                .updatedAt(currentDateTime)
                .build();

        AppUser savedAppUser = appUserRepository.save(appUser);

        return AppUserDTO.builder()
                .userId(savedAppUser.getUserId())
                .username(savedAppUser.getUsername())
                .email(savedAppUser.getEmail())
                .profilePicUrl(savedAppUser.getProfilePicUrl())
                .karma(savedAppUser.getKarma())
                .build();
    }

    public AppUserDTO updateAppUser(String username, AppUserUpdationDTO appUserUpdationDTO) {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User: {} does not exist", username);
            return new AppUserNotFoundException("User does not exist");
        });
        appUser.setEmail(appUserUpdationDTO.getEmail());
        appUser.setProfilePicUrl(appUserUpdationDTO.getProfilePicUrl());
        appUser.setUpdatedAt(LocalDateTime.now());
        AppUser updatedAppUser = appUserRepository.save(appUser);

        return AppUserDTO.builder()
                .userId(updatedAppUser.getUserId())
                .username(updatedAppUser.getUsername())
                .email(updatedAppUser.getEmail())
                .profilePicUrl(updatedAppUser.getProfilePicUrl())
                .karma(updatedAppUser.getKarma())
                .build();
    }

    public AppUserDTO getAppUser(String username) {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User: {} does not exist", username);
            return new AppUserNotFoundException("User does not exist");
        });

        return AppUserDTO.builder()
                .userId(appUser.getUserId())
                .username(appUser.getUsername())
                .email(appUser.getEmail())
                .profilePicUrl(appUser.getProfilePicUrl())
                .karma(appUser.getKarma())
                .build();
    }

    public List<AppUserDTO> getAppUsers(Pageable pageable) {
        if (pageable.isUnpaged()) {
            pageable = PageRequest.of(
                    AppConstants.DEFAULT_PAGE_NUMBER,
                    AppConstants.DEFAULT_PAGE_SIZE,
                    Sort.by(AppConstants.DEFAULT_SORT_BY)
            );
        }

        Page<AppUser> appUserPage = appUserRepository.findAll(pageable);
        return appUserPage.stream()
                .map(appUser -> AppUserDTO.builder()
                        .userId(appUser.getUserId())
                        .username(appUser.getUsername())
                        .email(appUser.getEmail())
                        .profilePicUrl(appUser.getProfilePicUrl())
                        .karma(appUser.getKarma())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteAppUser(String username) {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User: {} does not exist", username);
            return new AppUserNotFoundException("User does not exist");
        });
        appUserRepository.deleteById(appUser.getUserId());
    }

    public List<PostDTO> getPostsByUser(String username, Pageable pageable) {
        if (!appUserRepository.existsByUsername(username)) {
            log.error("User: {} does not exist", username);
            throw new AppUserNotFoundException("User does not exist");
        }

        Page<Post> postsPage = postRepository.findAllByAuthor_Username(username, pageable);
        return postsPage.stream()
                .map(post -> PostDTO.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .author(post.getAuthor().getUsername())
                        .content(post.getContent())
                        .community(post.getCommunity().getCommunityName())
                        .build())
                .collect(Collectors.toList());
    }
}
