package com.goutham.redditservice.controller;

import com.goutham.redditservice.dto.AppUserCreationDTO;
import com.goutham.redditservice.dto.AppUserDTO;
import com.goutham.redditservice.dto.AppUserUpdationDTO;
import com.goutham.redditservice.dto.PostDTO;
import com.goutham.redditservice.service.AppUserService;
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
}
