package com.goutham.redditservice.controller;

import com.goutham.redditservice.dto.PostCreationDTO;
import com.goutham.redditservice.dto.PostDTO;
import com.goutham.redditservice.dto.VoteDTO;
import com.goutham.redditservice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public EntityModel<PostDTO> createPost(@RequestBody PostCreationDTO postCreationDTO) {
        PostDTO postDTO = postService.createPost(postCreationDTO);
        return EntityModel.of(
                postDTO,
                linkTo(methodOn(PostController.class).getPost(postDTO.getPostId())).withSelfRel(),
                linkTo(methodOn(CommunityController.class).getPosts(postDTO.getCommunity(), Pageable.unpaged())).withRel("communityPosts"),
                linkTo(methodOn(AppUserController.class).getPosts(postDTO.getAuthor(), Pageable.unpaged())).withRel("userPosts")
        );
    }

    @GetMapping("/{postId}")
    public EntityModel<PostDTO> getPost(@PathVariable Long postId) {
        PostDTO postDTO = postService.getPost(postId);
        return EntityModel.of(
                postDTO,
                linkTo(methodOn(PostController.class).getPost(postDTO.getPostId())).withSelfRel(),
                linkTo(methodOn(CommunityController.class).getPosts(postDTO.getCommunity(), Pageable.unpaged())).withRel("communityPosts"),
                linkTo(methodOn(AppUserController.class).getPosts(postDTO.getAuthor(), Pageable.unpaged())).withRel("userPosts")
        );
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
    }

    @PostMapping("/{postId}/upvote")
    public EntityModel<PostDTO> upvotePost(@PathVariable Long postId, @RequestBody VoteDTO voteDTO) {
        PostDTO postDTO = postService.upvotePost(postId, voteDTO.getUsername());
        return EntityModel.of(
                postDTO,
                linkTo(methodOn(PostController.class).getPost(postDTO.getPostId())).withSelfRel(),
                linkTo(methodOn(CommunityController.class).getPosts(postDTO.getCommunity(), Pageable.unpaged())).withRel("communityPosts"),
                linkTo(methodOn(AppUserController.class).getPosts(postDTO.getAuthor(), Pageable.unpaged())).withRel("userPosts")
        );
    }

    @PostMapping("/{postId}/downvote")
    public EntityModel<PostDTO> downvotePost(@PathVariable Long postId, @RequestBody VoteDTO voteDTO) {
        PostDTO postDTO = postService.downvotePost(postId, voteDTO.getUsername());
        return EntityModel.of(
                postDTO,
                linkTo(methodOn(PostController.class).getPost(postDTO.getPostId())).withSelfRel(),
                linkTo(methodOn(CommunityController.class).getPosts(postDTO.getCommunity(), Pageable.unpaged())).withRel("communityPosts"),
                linkTo(methodOn(AppUserController.class).getPosts(postDTO.getAuthor(), Pageable.unpaged())).withRel("userPosts")
        );
    }

    @DeleteMapping("/{postId}/upvote/{username}")
    public EntityModel<PostDTO> removeUpvote(@PathVariable Long postId, @PathVariable String username) {
        PostDTO postDTO = postService.removeUpvote(postId, username);
        return EntityModel.of(
                postDTO,
                linkTo(methodOn(PostController.class).getPost(postDTO.getPostId())).withSelfRel(),
                linkTo(methodOn(CommunityController.class).getPosts(postDTO.getCommunity(), Pageable.unpaged())).withRel("communityPosts"),
                linkTo(methodOn(AppUserController.class).getPosts(postDTO.getAuthor(), Pageable.unpaged())).withRel("userPosts")
        );
    }

    @DeleteMapping("/{postId}/downvote/{username}")
    public EntityModel<PostDTO> removeDownvote(@PathVariable Long postId, @PathVariable String username) {
        PostDTO postDTO = postService.removeDownvote(postId, username);
        return EntityModel.of(
                postDTO,
                linkTo(methodOn(PostController.class).getPost(postDTO.getPostId())).withSelfRel(),
                linkTo(methodOn(CommunityController.class).getPosts(postDTO.getCommunity(), Pageable.unpaged())).withRel("communityPosts"),
                linkTo(methodOn(AppUserController.class).getPosts(postDTO.getAuthor(), Pageable.unpaged())).withRel("userPosts")
        );
    }
}
