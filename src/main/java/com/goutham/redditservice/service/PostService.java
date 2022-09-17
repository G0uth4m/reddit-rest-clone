package com.goutham.redditservice.service;

import com.goutham.redditservice.dto.PostCreationDTO;
import com.goutham.redditservice.dto.PostDTO;
import com.goutham.redditservice.dto.VoteDTO;
import com.goutham.redditservice.entity.AppUser;
import com.goutham.redditservice.entity.Community;
import com.goutham.redditservice.entity.Post;
import com.goutham.redditservice.entity.PostVote;
import com.goutham.redditservice.enums.VoteTypeEnum;
import com.goutham.redditservice.exception.AppUserNotMemberOfCommunityException;
import com.goutham.redditservice.exception.DuplicateVoteException;
import com.goutham.redditservice.exception.PostNotFoundException;
import com.goutham.redditservice.exception.VoteNotFoundException;
import com.goutham.redditservice.key.CommunityUserAssociationKey;
import com.goutham.redditservice.key.PostVoteKey;
import com.goutham.redditservice.repository.CommunityUserAssociationRepository;
import com.goutham.redditservice.repository.PostRepository;
import com.goutham.redditservice.repository.PostVoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Autowired
    private CommunityUserAssociationRepository communityUserAssociationRepository;

    public PostDTO createPost(PostCreationDTO postCreationDTO) {
        AppUser appUser = appUserService.getUserDAO(postCreationDTO.getAuthor());
        Community community = communityService.getCommunityDAO(postCreationDTO.getCommunity());

        CommunityUserAssociationKey communityUserAssociationKey = CommunityUserAssociationKey.builder()
                .communityId(community.getCommunityId())
                .userId(appUser.getUserId())
                .build();
        if (!communityUserAssociationRepository.existsById(communityUserAssociationKey)) {
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

        CompletableFuture.supplyAsync(() -> {
            PostVote postVote = PostVote.builder()
                    .postVoteKey(PostVoteKey.builder()
                            .userId(appUser.getUserId())
                            .postId(savedPost.getPostId())
                            .build())
                    .voteType(VoteTypeEnum.UPVOTE.value())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            return postVoteRepository.save(postVote);
        }).whenComplete((postVote, throwable) -> {
            if (Objects.nonNull(throwable)) {
                log.error("Failed while adding default upvote to post: {} by author: {} with error: ",
                        postVote.getPostVoteKey().getPostId(), postVote.getPostVoteKey().getUserId(), throwable);
            } else {
                log.info("Added default upvote for post: {} by author: {}",
                        postVote.getPostVoteKey().getPostId(), postVote.getPostVoteKey().getUserId());
            }
        });

        return PostDTO.builder()
                .postId(savedPost.getPostId())
                .title(savedPost.getTitle())
                .author(appUser.getUsername())
                .content(savedPost.getContent())
                .community(community.getCommunityName())
                .votes(1L)
                .createdAt(savedPost.getCreatedAt())
                .build();
    }

    public PostDTO getPost(Long postId) {
        return postRepository.findPostDTOById(postId).orElseThrow(() -> {
            log.error("Post with id: {} does not exist", postId);
            return new PostNotFoundException("Post does not exist");
        });
    }

    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            log.error("Post with id: {} does not exist", postId);
            throw new PostNotFoundException("Post does not exist");
        }
        postRepository.deleteById(postId);
    }

    public PostDTO votePost(Long postId, VoteDTO voteDTO) {
        Post post = getPostDAO(postId);
        AppUser appUser = appUserService.getUserDAO(voteDTO.getUsername());

        PostVoteKey postVoteKey = PostVoteKey.builder()
                .postId(post.getPostId())
                .userId(appUser.getUserId())
                .build();

        Optional<PostVote> voteOptional = postVoteRepository.findById(postVoteKey);
        if (voteOptional.isPresent()) {
            PostVote postVote = voteOptional.get();
            if (voteDTO.getVoteType().value().equals(postVote.getVoteType())) {
                log.error("Duplicate vote by user: {} on post: {}", voteDTO.getUsername(), post.getPostId());
                throw new DuplicateVoteException("Duplicate vote");
            } else {
                postVote.setVoteType(voteDTO.getVoteType().value());
                postVote.setUpdatedAt(LocalDateTime.now());
                postVoteRepository.save(postVote);
            }
        } else {
            LocalDateTime now = LocalDateTime.now();
            PostVote postVote = PostVote.builder()
                    .postVoteKey(postVoteKey)
                    .voteType(voteDTO.getVoteType().value())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            postVoteRepository.save(postVote);
        }

        return PostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .author(post.getAuthor().getUsername())
                .content(post.getContent())
                .community(post.getCommunity().getCommunityName())
                .votes(postVoteRepository.getVoteCountByPostId(postId))
                .createdAt(post.getCreatedAt())
                .build();
    }

    public PostDTO removeVote(Long postId, String username) {
        Post post = getPostDAO(postId);
        AppUser appUser = appUserService.getUserDAO(username);

        PostVoteKey postVoteKey = PostVoteKey.builder()
                .postId(post.getPostId())
                .userId(appUser.getUserId())
                .build();

        if (!postVoteRepository.existsById(postVoteKey)) {
            log.error("Vote not found for post: {} by user: {}", postId, username);
            throw new VoteNotFoundException("Vote not found");
        }
        postVoteRepository.deleteById(postVoteKey);

        return PostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .author(post.getAuthor().getUsername())
                .content(post.getContent())
                .community(post.getCommunity().getCommunityName())
                .votes(postVoteRepository.getVoteCountByPostId(postId))
                .createdAt(post.getCreatedAt())
                .build();
    }

    public Post getPostDAO(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with id: {} does not exist", postId);
            return new PostNotFoundException("Post does not exist");
        });
    }
}
