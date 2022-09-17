package com.goutham.redditservice.service;

import com.goutham.redditservice.dto.CommentCreationDTO;
import com.goutham.redditservice.dto.CommentDTO;
import com.goutham.redditservice.dto.CommentUpdationDTO;
import com.goutham.redditservice.dto.VoteDTO;
import com.goutham.redditservice.entity.AppUser;
import com.goutham.redditservice.entity.Comment;
import com.goutham.redditservice.entity.CommentVote;
import com.goutham.redditservice.entity.Post;
import com.goutham.redditservice.enums.VoteTypeEnum;
import com.goutham.redditservice.exception.CommentNotFoundException;
import com.goutham.redditservice.exception.DuplicateVoteException;
import com.goutham.redditservice.exception.PostNotFoundException;
import com.goutham.redditservice.exception.VoteNotFoundException;
import com.goutham.redditservice.key.CommentVoteKey;
import com.goutham.redditservice.repository.CommentRepository;
import com.goutham.redditservice.repository.CommentVoteRepository;
import com.goutham.redditservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentVoteRepository commentVoteRepository;

    @Autowired
    private PostRepository postRepository;

    public CommentDTO createComment(Long postId, CommentCreationDTO commentCreationDTO) {
        AppUser appUser = appUserService.getUserDAO(commentCreationDTO.getAuthor());
        Post post = postService.getPostDAO(postId);

        LocalDateTime now = LocalDateTime.now();
        Comment comment = Comment.builder()
                .content(commentCreationDTO.getContent())
                .post(post)
                .author(appUser)
                .isDeleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Comment savedComment = commentRepository.save(comment);

        CompletableFuture.supplyAsync(() -> {
            CommentVote commentVote = CommentVote.builder()
                    .commentVoteKey(CommentVoteKey.builder()
                            .userId(appUser.getUserId())
                            .commentId(savedComment.getCommentId())
                            .build())
                    .voteType(VoteTypeEnum.UPVOTE.value())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            return commentVoteRepository.save(commentVote);
        }).whenComplete((commentVote, throwable) -> {
            if (Objects.nonNull(throwable)) {
                log.error("Failed while adding default upvote to comment: {} by author: {} with error: ",
                        commentVote.getCommentVoteKey().getCommentId(), commentVote.getCommentVoteKey().getUserId(),
                        throwable);
            } else {
                log.info("Added default upvote for comment: {} by author: {}",
                        commentVote.getCommentVoteKey().getCommentId(), commentVote.getCommentVoteKey().getUserId());
            }
        });

        return CommentDTO.builder()
                .commentId(savedComment.getCommentId())
                .content(savedComment.getContent())
                .author(savedComment.getAuthor().getUsername())
                .votes(1L)
                .createdAt(savedComment.getCreatedAt())
                .updatedAt(savedComment.getUpdatedAt())
                .build();
    }

    public CommentDTO editComment(Long commentId, CommentUpdationDTO commentUpdationDTO) {
        Comment comment = getCommentDAO(commentId);
        comment.setContent(commentUpdationDTO.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        return CommentDTO.builder()
                .commentId(savedComment.getCommentId())
                .content(savedComment.getContent())
                .author(savedComment.getAuthor().getUsername())
                .votes(commentVoteRepository.getVoteCountByCommentId(commentId))
                .createdAt(savedComment.getCreatedAt())
                .updatedAt(savedComment.getUpdatedAt())
                .build();
    }

    public List<CommentDTO> getPostComments(Long postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            log.error("Post with id: {} does not exist", postId);
            throw new PostNotFoundException("Post does not exist");
        }
        return commentRepository.findAllByPostId(postId, pageable).toList();
    }

    public List<CommentDTO> getUserComments(String username, Pageable pageable) {
        AppUser appUser = appUserService.getUserDAO(username);
        return commentRepository.findAllByCommenterId(appUser.getUserId(), pageable).toList();
    }

    public CommentDTO getComment(Long commentId) {
        return commentRepository.findByCommentId(commentId).orElseThrow(() -> {
            log.error("Comment with id: {} does not exist", commentId);
            return new CommentNotFoundException("Comment does not exist");
        });
    }

    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            log.error("Comment with id: {} does not exist", commentId);
            throw new CommentNotFoundException("Comment does not exist");
        }
        commentRepository.deleteById(commentId);
    }

    public CommentDTO voteComment(Long commentId, VoteDTO voteDTO) {
        Comment comment = getCommentDAO(commentId);
        AppUser appUser = appUserService.getUserDAO(voteDTO.getUsername());

        CommentVoteKey commentVoteKey = CommentVoteKey.builder()
                .commentId(comment.getCommentId())
                .userId(appUser.getUserId())
                .build();

        Optional<CommentVote> voteOptional = commentVoteRepository.findById(commentVoteKey);
        if (voteOptional.isPresent()) {
            CommentVote commentVote = voteOptional.get();
            if (voteDTO.getVoteType().value().equals(commentVote.getVoteType())) {
                log.error("Duplicate vote by user: {} on comment: {}", voteDTO.getUsername(), comment.getCommentId());
                throw new DuplicateVoteException("Duplicate vote");
            } else {
                commentVote.setVoteType(voteDTO.getVoteType().value());
                commentVote.setUpdatedAt(LocalDateTime.now());
                commentVoteRepository.save(commentVote);
            }
        } else {
            LocalDateTime now = LocalDateTime.now();
            CommentVote commentVote = CommentVote.builder()
                    .commentVoteKey(commentVoteKey)
                    .voteType(voteDTO.getVoteType().value())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            commentVoteRepository.save(commentVote);
        }

        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .author(comment.getAuthor().getUsername())
                .votes(commentVoteRepository.getVoteCountByCommentId(commentId))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public CommentDTO removeVote(Long commentId, String username) {
        Comment comment = getCommentDAO(commentId);
        AppUser appUser = appUserService.getUserDAO(username);

        CommentVoteKey commentVoteKey = CommentVoteKey.builder()
                .commentId(comment.getCommentId())
                .userId(appUser.getUserId())
                .build();

        if (!commentVoteRepository.existsById(commentVoteKey)) {
            log.error("Vote not found for comment: {} by user: {}", commentId, username);
            throw new VoteNotFoundException("Vote not found");
        }
        commentVoteRepository.deleteById(commentVoteKey);

        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .author(comment.getAuthor().getUsername())
                .votes(commentVoteRepository.getVoteCountByCommentId(commentId))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public Comment getCommentDAO(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("Comment with id: {} does not exist", commentId);
            return new CommentNotFoundException("Comment does not exist");
        });
    }
}
