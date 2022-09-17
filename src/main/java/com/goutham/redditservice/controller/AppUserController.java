package com.goutham.redditservice.controller;

import com.goutham.redditservice.dto.AppUserCreationDTO;
import com.goutham.redditservice.dto.AppUserDTO;
import com.goutham.redditservice.dto.AppUserUpdationDTO;
import com.goutham.redditservice.dto.CommentDTO;
import com.goutham.redditservice.dto.CommunityDTO;
import com.goutham.redditservice.dto.PostDTO;
import com.goutham.redditservice.service.AppUserService;
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
@RequestMapping("/v1/api/users")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private CommentService commentService;


    @PostMapping
    public EntityModel<AppUserDTO> createAppUser(@RequestBody AppUserCreationDTO appUserCreationDTO) {
        AppUserDTO appUserDTO = appUserService.createAppUser(appUserCreationDTO);
        return EntityModel.of(
                appUserDTO,
                linkTo(methodOn(AppUserController.class).getAppUser(appUserDTO.getUsername())).withSelfRel(),
                linkTo(methodOn(AppUserController.class).getAppUsers(Pageable.unpaged())).withRel("users")
        );
    }

    @PutMapping("/{username}")
    public EntityModel<AppUserDTO> updateAppUser(
            @PathVariable String username,
            @RequestBody AppUserUpdationDTO appUserUpdationDTO
    ) {
        AppUserDTO appUserDTO = appUserService.updateAppUser(username, appUserUpdationDTO);
        return EntityModel.of(
                appUserDTO,
                linkTo(methodOn(AppUserController.class).getAppUser(username)).withSelfRel(),
                linkTo(methodOn(AppUserController.class).getAppUsers(Pageable.unpaged())).withRel("users")
        );
    }

    @GetMapping("/{username}")
    public EntityModel<AppUserDTO> getAppUser(@PathVariable String username) {
        AppUserDTO appUserDTO = appUserService.getAppUser(username);
        return EntityModel.of(
                appUserDTO,
                linkTo(methodOn(AppUserController.class).getAppUser(username)).withSelfRel(),
                linkTo(methodOn(AppUserController.class).getAppUsers(Pageable.unpaged())).withRel("users")
        );
    }

    @GetMapping
    public CollectionModel<EntityModel<AppUserDTO>> getAppUsers(@ParameterObject Pageable pageable) {
        List<AppUserDTO> appUsers = appUserService.getAppUsers(pageable);

        List<EntityModel<AppUserDTO>> appUserEntityModels = appUsers.stream()
                .map(appUserDTO -> EntityModel.of(
                        appUserDTO,
                        linkTo(methodOn(AppUserController.class).getAppUser(appUserDTO.getUsername())).withSelfRel(),
                        linkTo(methodOn(AppUserController.class).getAppUsers(Pageable.unpaged())).withRel("users")))
                .collect(Collectors.toList());

        return CollectionModel.of(
                appUserEntityModels,
                linkTo(methodOn(AppUserController.class).getAppUsers(pageable)).withSelfRel()
        );
    }

    @DeleteMapping("/{username}")
    public void deleteAppUser(@PathVariable String username) {
        appUserService.deleteAppUser(username);
    }

    @GetMapping("/{username}/posts")
    public CollectionModel<EntityModel<PostDTO>> getPosts(@PathVariable String username, @ParameterObject Pageable pageable) {
        List<PostDTO> posts = appUserService.getPostsByUser(username, pageable);

        List<EntityModel<PostDTO>> postEntityModels = posts.stream()
                .map(postDTO -> EntityModel.of(
                        postDTO,
                        linkTo(methodOn(PostController.class).getPost(postDTO.getPostId())).withSelfRel(),
                        linkTo(methodOn(AppUserController.class).getPosts(username, Pageable.unpaged())).withRel("posts")))
                .collect(Collectors.toList());

        return CollectionModel.of(
                postEntityModels,
                linkTo(methodOn(AppUserController.class).getPosts(username, Pageable.unpaged())).withRel("posts")
        );
    }

    @GetMapping("/{username}/communities")
    public CollectionModel<EntityModel<CommunityDTO>> getUserCommunities(
            @PathVariable String username, @ParameterObject Pageable pageable
    ) {
        List<CommunityDTO> communities = appUserService.getUserCommunities(username, pageable);

        List<EntityModel<CommunityDTO>> communityEntityModels = communities.stream()
                .map(communityDTO -> EntityModel.of(
                        communityDTO,
                        linkTo(methodOn(CommunityController.class).getCommunity(communityDTO.getCommunityName()))
                                .withSelfRel(),
                        linkTo(methodOn(AppUserController.class).getUserCommunities(username, Pageable.unpaged()))
                                .withRel("user-communities")
                )).collect(Collectors.toList());
        
        return CollectionModel.of(
                communityEntityModels,
                linkTo(methodOn(AppUserController.class).getUserCommunities(username, Pageable.unpaged()))
                        .withRel("user-communities")
        );
    }

    @GetMapping("/{username}/comments")
    public CollectionModel<EntityModel<CommentDTO>> getUserComments(
            @PathVariable String username,
            @ParameterObject Pageable pageable
    ) {
        List<CommentDTO> comments = commentService.getUserComments(username, pageable);
        List<EntityModel<CommentDTO>> commentsEntityModels = comments.stream()
                .map(commentDTO -> EntityModel.of(
                        commentDTO,
                        linkTo(methodOn(CommentController.class).getComment(commentDTO.getCommentId()))
                                .withSelfRel(),
                        linkTo(methodOn(AppUserController.class).getUserComments(username, Pageable.unpaged()))
                                .withRel("user-comments")
                )).collect(Collectors.toList());

        return CollectionModel.of(
                commentsEntityModels,
                linkTo(methodOn(AppUserController.class).getUserComments(username, Pageable.unpaged())).withSelfRel()
        );
    }
}
