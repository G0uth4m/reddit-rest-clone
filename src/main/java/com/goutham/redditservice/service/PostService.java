package com.goutham.redditservice.service;

import com.goutham.redditservice.dto.PostCreationDTO;
import com.goutham.redditservice.dto.PostDTO;
import com.goutham.redditservice.entity.AppUser;
import com.goutham.redditservice.entity.Community;
import com.goutham.redditservice.entity.Post;
import com.goutham.redditservice.exception.AppUserNotFoundException;
import com.goutham.redditservice.exception.AppUserNotMemberOfCommunityException;
import com.goutham.redditservice.exception.CommunityNotFoundException;
import com.goutham.redditservice.exception.PostNotFoundException;
import com.goutham.redditservice.repository.AppUserRepository;
import com.goutham.redditservice.repository.CommunityRepository;
import com.goutham.redditservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CommunityRepository communityRepository;

    public PostDTO createPost(PostCreationDTO postCreationDTO) {
        AppUser appUser = appUserRepository.findByUsername(postCreationDTO.getAuthor()).orElseThrow(() -> {
            log.error("User: {} does not exist", postCreationDTO.getAuthor());
            return new AppUserNotFoundException("User does not exist");
        });

        Community community = communityRepository.findByCommunityName(postCreationDTO.getCommunity()).orElseThrow(() -> {
            log.error("Community: {} does not exist", postCreationDTO.getCommunity());
            return new CommunityNotFoundException("Community does not exist");
        });

        if (!community.getMembers().contains(appUser)) {
            log.error("User: {} not member of community: {}", appUser.getUsername(), community.getCommunityName());
            throw new AppUserNotMemberOfCommunityException("User not member of community");
        }

        LocalDateTime now = LocalDateTime.now();
        Post post = Post.builder()
                .title(postCreationDTO.getTitle())
                .content(postCreationDTO.getContent())
                .author(appUser)
                .community(community)
                .isDeleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Post savedPost = postRepository.save(post);
        return PostDTO.builder()
                .postId(savedPost.getPostId())
                .title(savedPost.getTitle())
                .author(savedPost.getAuthor().getUsername())
                .content(savedPost.getContent())
                .community(savedPost.getCommunity().getCommunityName())
                .build();
    }

    public PostDTO getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with id: {} does not exist", postId);
            return new PostNotFoundException("Post does not exist");
        });

        return PostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .author(post.getAuthor().getUsername())
                .content(post.getContent())
                .community(post.getCommunity().getCommunityName())
                .build();
    }

    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            log.error("Post with id: {} does not exist", postId);
            throw new PostNotFoundException("Post does not exist");
        }
        postRepository.deleteById(postId);
    }
}
