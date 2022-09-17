package com.goutham.redditservice.controller;

import com.goutham.redditservice.dto.CommentDTO;
import com.goutham.redditservice.dto.CommentUpdationDTO;
import com.goutham.redditservice.dto.VoteDTO;
import com.goutham.redditservice.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PutMapping("/{commentId}")
    public EntityModel<CommentDTO> editComment(
            @PathVariable Long commentId,
            @RequestBody CommentUpdationDTO commentUpdationDTO
    ) {
        CommentDTO commentDTO = commentService.editComment(commentId, commentUpdationDTO);
        return EntityModel.of(
                commentDTO,
                linkTo(methodOn(CommentController.class).getComment(commentDTO.getCommentId()))
                        .withSelfRel()
        );
    }

    @GetMapping("/{commentId}")
    public EntityModel<CommentDTO> getComment(@PathVariable Long commentId) {
        CommentDTO commentDTO = commentService.getComment(commentId);
        return EntityModel.of(
                commentDTO,
                linkTo(methodOn(CommentController.class).getComment(commentDTO.getCommentId()))
                        .withSelfRel()
        );
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    @PostMapping("/{commentId}/vote")
    public EntityModel<CommentDTO> voteComment(@PathVariable Long commentId, @Valid @RequestBody VoteDTO voteDTO) {
        CommentDTO commentDTO = commentService.voteComment(commentId, voteDTO);
        return EntityModel.of(
                commentDTO,
                linkTo(methodOn(CommentController.class).getComment(commentDTO.getCommentId()))
                        .withSelfRel()
        );
    }

    @DeleteMapping("/{commentId}/vote/{username}")
    public EntityModel<CommentDTO> removeVote(@PathVariable Long commentId, @PathVariable String username) {
        CommentDTO commentDTO = commentService.removeVote(commentId, username);
        return EntityModel.of(
                commentDTO,
                linkTo(methodOn(CommentController.class).getComment(commentDTO.getCommentId()))
                        .withSelfRel()
        );
    }
}
