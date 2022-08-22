package com.goutham.redditservice.service;

import com.goutham.redditservice.dto.CommentCreationDTO;
import com.goutham.redditservice.dto.CommentDTO;
import com.goutham.redditservice.dto.CommentUpdationDTO;
import com.goutham.redditservice.entity.AppUser;
import com.goutham.redditservice.entity.Comment;
import com.goutham.redditservice.entity.Post;
import com.goutham.redditservice.exception.CommentNotFoundException;
import com.goutham.redditservice.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PostService postService;

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

        return CommentDTO.builder()
                .commentId(savedComment.getCommentId())
                .content(savedComment.getContent())
                .author(savedComment.getAuthor().getUsername())
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
                .createdAt(savedComment.getCreatedAt())
                .updatedAt(savedComment.getUpdatedAt())
                .build();
    }

    public List<CommentDTO> getComments(Long postId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findAllByPost_PostId(postId, pageable);
        return commentPage.stream()
                .map(comment -> CommentDTO.builder()
                        .commentId(comment.getCommentId())
                        .content(comment.getContent())
                        .author(comment.getAuthor().getUsername())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public CommentDTO getComment(Long commentId) {
        Comment comment = getCommentDAO(commentId);
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .author(comment.getAuthor().getUsername())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            log.error("Comment with id: {} does not exist", commentId);
            throw new CommentNotFoundException("Comment does not exist");
        }
        commentRepository.deleteById(commentId);
    }

    public Comment getCommentDAO(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("Comment with id: {} does not exist", commentId);
            return new CommentNotFoundException("Comment does not exist");
        });
    }
}
