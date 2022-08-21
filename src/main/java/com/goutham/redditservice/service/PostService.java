package com.goutham.redditservice.service;

import com.goutham.redditservice.dto.PostCreationDTO;
import com.goutham.redditservice.dto.PostDTO;
import com.goutham.redditservice.entity.AppUser;
import com.goutham.redditservice.entity.Community;
import com.goutham.redditservice.entity.Post;
import com.goutham.redditservice.exception.AppUserNotFoundException;
import com.goutham.redditservice.exception.AppUserNotMemberOfCommunityException;
import com.goutham.redditservice.exception.CommunityNotFoundException;
import com.goutham.redditservice.exception.DuplicateVoteException;
import com.goutham.redditservice.exception.PostNotFoundException;
import com.goutham.redditservice.exception.VoteNotFoundException;
import com.goutham.redditservice.repository.AppUserRepository;
import com.goutham.redditservice.repository.CommunityRepository;
import com.goutham.redditservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

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
                .upvotes(Collections.singleton(appUser))
                .downvotes(Collections.emptySet())
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
                .votes((long) (savedPost.getUpvotes().size() - savedPost.getDownvotes().size()))
                .createdAt(savedPost.getCreatedAt())
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
                .votes((long) (post.getUpvotes().size() - post.getDownvotes().size()))
                .createdAt(post.getCreatedAt())
                .build();
    }

    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            log.error("Post with id: {} does not exist", postId);
            throw new PostNotFoundException("Post does not exist");
        }
        postRepository.deleteById(postId);
    }

    public PostDTO upvotePost(Long postId, String username) {
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with id: {} does not exist", postId);
            return new PostNotFoundException("Post does not exist");
        });

        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User: {} does not exist", username);
            return new AppUserNotFoundException("User does not exist");
        });

        if (post.getUpvotes().contains(appUser)) {
            log.error("Duplicate upvote by user: {} on post: {}", username, post);
            throw new DuplicateVoteException("Duplicate vote");
        }

        post.getDownvotes().remove(appUser);
        post.getUpvotes().add(appUser);
        Post savedPost = postRepository.save(post);

        return PostDTO.builder()
                .postId(savedPost.getPostId())
                .title(savedPost.getTitle())
                .author(savedPost.getAuthor().getUsername())
                .content(savedPost.getContent())
                .community(savedPost.getCommunity().getCommunityName())
                .votes((long) (savedPost.getUpvotes().size() - savedPost.getDownvotes().size()))
                .createdAt(savedPost.getCreatedAt())
                .build();
    }

    public PostDTO removeUpvote(Long postId, String username) {
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with id: {} does not exist", postId);
            return new PostNotFoundException("Post does not exist");
        });

        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User: {} does not exist", username);
            return new AppUserNotFoundException("User does not exist");
        });

        if (!post.getUpvotes().contains(appUser)) {
            log.error("Upvote not found for post: {} by user: {}", postId, username);
            throw new VoteNotFoundException("Upvote not found");
        }

        post.getUpvotes().remove(appUser);
        Post savedPost = postRepository.save(post);

        return PostDTO.builder()
                .postId(savedPost.getPostId())
                .title(savedPost.getTitle())
                .author(savedPost.getAuthor().getUsername())
                .content(savedPost.getContent())
                .community(savedPost.getCommunity().getCommunityName())
                .votes((long) (savedPost.getUpvotes().size() - savedPost.getDownvotes().size()))
                .createdAt(savedPost.getCreatedAt())
                .build();
    }

    public PostDTO downvotePost(Long postId, String username) {
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with id: {} does not exist", postId);
            return new PostNotFoundException("Post does not exist");
        });

        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User: {} does not exist", username);
            return new AppUserNotFoundException("User does not exist");
        });

        if (post.getDownvotes().contains(appUser)) {
            log.error("Duplicate upvote by user: {} on post: {}", username, post);
            throw new DuplicateVoteException("Duplicate vote");
        }

        post.getUpvotes().remove(appUser);
        post.getDownvotes().add(appUser);
        Post savedPost = postRepository.save(post);

        return PostDTO.builder()
                .postId(savedPost.getPostId())
                .title(savedPost.getTitle())
                .author(savedPost.getAuthor().getUsername())
                .content(savedPost.getContent())
                .community(savedPost.getCommunity().getCommunityName())
                .votes((long) (savedPost.getUpvotes().size() - savedPost.getDownvotes().size()))
                .createdAt(savedPost.getCreatedAt())
                .build();
    }

    public PostDTO removeDownvote(Long postId, String username) {
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with id: {} does not exist", postId);
            return new PostNotFoundException("Post does not exist");
        });

        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User: {} does not exist", username);
            return new AppUserNotFoundException("User does not exist");
        });

        if (!post.getDownvotes().contains(appUser)) {
            log.error("Downvote not found for post: {} by user: {}", postId, username);
            throw new VoteNotFoundException("Downvote not found");
        }

        post.getDownvotes().remove(appUser);
        Post savedPost = postRepository.save(post);

        return PostDTO.builder()
                .postId(savedPost.getPostId())
                .title(savedPost.getTitle())
                .author(savedPost.getAuthor().getUsername())
                .content(savedPost.getContent())
                .community(savedPost.getCommunity().getCommunityName())
                .votes((long) (savedPost.getUpvotes().size() - savedPost.getDownvotes().size()))
                .createdAt(savedPost.getCreatedAt())
                .build();
    }
}
