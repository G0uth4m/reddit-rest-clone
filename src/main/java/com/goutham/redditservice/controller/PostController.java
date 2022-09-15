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

    @PostMapping("/{postId}/vote")
    public EntityModel<PostDTO> votePost(@PathVariable Long postId, @RequestBody VoteDTO voteDTO) {
        PostDTO postDTO = postService.votePost(postId, voteDTO);
        return EntityModel.of(
                postDTO,
                linkTo(methodOn(PostController.class).getPost(postDTO.getPostId())).withSelfRel(),
                linkTo(methodOn(CommunityController.class).getPosts(postDTO.getCommunity(), Pageable.unpaged())).withRel("communityPosts"),
                linkTo(methodOn(AppUserController.class).getPosts(postDTO.getAuthor(), Pageable.unpaged())).withRel("userPosts")
        );
    }

    @DeleteMapping("/{postId}/vote/{username}")
    public EntityModel<PostDTO> removeVote(@PathVariable Long postId, @PathVariable String username) {
        PostDTO postDTO = postService.removeVote(postId, username);
        return EntityModel.of(
                postDTO,
                linkTo(methodOn(PostController.class).getPost(postDTO.getPostId())).withSelfRel(),
                linkTo(methodOn(CommunityController.class).getPosts(postDTO.getCommunity(), Pageable.unpaged())).withRel("communityPosts"),
                linkTo(methodOn(AppUserController.class).getPosts(postDTO.getAuthor(), Pageable.unpaged())).withRel("userPosts")
        );
    }
}
