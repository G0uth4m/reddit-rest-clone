package com.goutham.redditservice.controller;

import com.goutham.redditservice.dto.AppUserDTO;
import com.goutham.redditservice.dto.CommunityCreationDTO;
import com.goutham.redditservice.dto.CommunityDTO;
import com.goutham.redditservice.dto.CommunityJoinDTO;
import com.goutham.redditservice.dto.CommunityUpdationDTO;
import com.goutham.redditservice.service.CommunityService;
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
@RequestMapping("/v1/api/communities")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @PostMapping
    public EntityModel<CommunityDTO> createCommunity(@RequestBody CommunityCreationDTO communityCreationDTO) {
        CommunityDTO communityDTO = communityService.createCommunity(communityCreationDTO);
        return EntityModel.of(
                communityDTO,
                linkTo(methodOn(CommunityController.class).getCommunity(communityDTO.getCommunityName())).withSelfRel(),
                linkTo(methodOn(CommunityController.class).getCommunities(Pageable.unpaged())).withRel("communities")
        );
    }

    @PutMapping("/{communityName}")
    public EntityModel<CommunityDTO> updateCommunity(
            @PathVariable String communityName,
            @RequestBody CommunityUpdationDTO communityUpdationDTO
    ) {
        CommunityDTO communityDTO = communityService.updateCommunity(communityName, communityUpdationDTO);
        return EntityModel.of(
                communityDTO,
                linkTo(methodOn(CommunityController.class).getCommunity(communityDTO.getCommunityName())).withSelfRel(),
                linkTo(methodOn(CommunityController.class).getCommunities(Pageable.unpaged())).withRel("communities")
        );
    }

    @GetMapping("/{communityName}")
    public EntityModel<CommunityDTO> getCommunity(@PathVariable String communityName) {
        CommunityDTO communityDTO = communityService.getCommunity(communityName);
        return EntityModel.of(
                communityDTO,
                linkTo(methodOn(CommunityController.class).getCommunity(communityDTO.getCommunityName())).withSelfRel(),
                linkTo(methodOn(CommunityController.class).getCommunities(Pageable.unpaged())).withRel("communities")
        );
    }

    @GetMapping
    public CollectionModel<EntityModel<CommunityDTO>> getCommunities(Pageable pageable) {
        List<CommunityDTO> communities = communityService.getCommunities(pageable);

        List<EntityModel<CommunityDTO>> communityEntityModels = communities.stream()
                .map(communityDTO -> EntityModel.of(
                        communityDTO,
                        linkTo(methodOn(CommunityController.class).getCommunity(communityDTO.getCommunityName())).withSelfRel(),
                        linkTo(methodOn(CommunityController.class).getCommunities(Pageable.unpaged())).withRel("communities")))
                .collect(Collectors.toList());

        return CollectionModel.of(
                communityEntityModels,
                linkTo(methodOn(CommunityController.class).getCommunities(Pageable.unpaged())).withSelfRel()
        );
    }

    @DeleteMapping("/{communityName}")
    public void deleteCommunity(@PathVariable String communityName) {
        communityService.deleteCommunity(communityName);
    }

    @GetMapping("/{communityName}/members")
    public CollectionModel<EntityModel<AppUserDTO>> getCommunityMembers(@PathVariable String communityName) {
        List<AppUserDTO> communityMembers = communityService.getCommunityMembers(communityName);

        List<EntityModel<AppUserDTO>> communityMemberEntityModels = communityMembers.stream()
                .map(communityMember -> EntityModel.of(
                        communityMember,
                        linkTo(methodOn(AppUserController.class).getAppUser(communityMember.getUsername())).withSelfRel(),
                        linkTo(methodOn(AppUserController.class).getAppUsers(Pageable.unpaged())).withRel("users")))
                .collect(Collectors.toList());

        return CollectionModel.of(
                communityMemberEntityModels,
                linkTo(methodOn(CommunityController.class).getCommunityMembers(communityName)).withSelfRel()
        );
    }

    @PostMapping("/{communityName}/members")
    public void joinCommunity(@PathVariable String communityName, @RequestBody CommunityJoinDTO communityJoinDTO) {
        communityService.addMemberToCommunity(communityName, communityJoinDTO);
    }

    @DeleteMapping("/{communityName}/members/{username}")
    public void leaveCommunity(@PathVariable String communityName, @PathVariable String username) {
        communityService.removeMemberFromCommunity(communityName, username);
    }
}
