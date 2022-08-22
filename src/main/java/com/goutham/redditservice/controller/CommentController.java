package com.goutham.redditservice.controller;

import com.goutham.redditservice.dto.CommentCreationDTO;
import com.goutham.redditservice.dto.CommentDTO;
import com.goutham.redditservice.dto.CommentUpdationDTO;
import com.goutham.redditservice.service.CommentService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/api/posts/{postId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public EntityModel<CommentDTO> createComment(
            @PathVariable Long postId,
            @RequestBody CommentCreationDTO commentCreationDTO
    ) {
        CommentDTO commentDTO = commentService.createComment(postId, commentCreationDTO);
        return EntityModel.of(
                commentDTO,
                linkTo(methodOn(CommentController.class).getComment(postId, commentDTO.getCommentId()))
                        .withSelfRel(),
                linkTo(methodOn(CommentController.class).getComments(postId, Pageable.unpaged()))
                        .withRel("post-comments")
        );
    }

    @PutMapping("/{commentId}")
    public EntityModel<CommentDTO> editComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentUpdationDTO commentUpdationDTO
    ) {
        CommentDTO commentDTO = commentService.editComment(commentId, commentUpdationDTO);
        return EntityModel.of(
                commentDTO,
                linkTo(methodOn(CommentController.class).getComment(postId, commentDTO.getCommentId()))
                        .withSelfRel(),
                linkTo(methodOn(CommentController.class).getComments(postId, Pageable.unpaged()))
                        .withRel("post-comments")
        );
    }

    @GetMapping("/{commentId}")
    public EntityModel<CommentDTO> getComment(@PathVariable Long postId, @PathVariable Long commentId) {
        CommentDTO commentDTO = commentService.getComment(commentId);
        return EntityModel.of(
                commentDTO,
                linkTo(methodOn(CommentController.class).getComment(postId, commentDTO.getCommentId()))
                        .withSelfRel(),
                linkTo(methodOn(CommentController.class).getComments(postId, Pageable.unpaged()))
                        .withRel("post-comments")
        );
    }

    @GetMapping
    public CollectionModel<EntityModel<CommentDTO>> getComments(@PathVariable Long postId, @ParameterObject Pageable pageable) {
        List<CommentDTO> comments = commentService.getComments(postId, pageable);
        List<EntityModel<CommentDTO>> commentEntityModels = comments.stream()
                .map(commentDTO -> EntityModel.of(
                        commentDTO,
                        linkTo(methodOn(CommentController.class).getComment(postId, commentDTO.getCommentId()))
                                .withSelfRel(),
                        linkTo(methodOn(CommentController.class).getComments(postId, Pageable.unpaged()))
                                .withRel("post-comments")))
                .collect(Collectors.toList());

        return CollectionModel.of(
                commentEntityModels,
                linkTo(methodOn(CommentController.class).getComments(postId, Pageable.unpaged())).withSelfRel()
        );
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
