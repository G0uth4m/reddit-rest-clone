package com.goutham.redditservice.constant;

public class SqlQueries {

    public static final String GET_VOTE_COUNT_BY_POST_ID =
        "SELECT " +
            "SUM(vt.voteTypeValue) " +
        "FROM " +
            "Vote v " +
        "LEFT JOIN VoteType vt " +
            "ON v.voteType.voteTypeId = vt.voteTypeId " +
        "WHERE " +
            "v.voteKey.postId = :postId";

    public static final String GET_POST_DTO_BY_ID =
        "SELECT new com.goutham.redditservice.dto.PostDTO(" +
            "p.postId, " +
            "p.title, " +
            "p.content, " +
            "au.username, " +
            "c.communityName, " +
            "SUM(vt.voteTypeValue), " +
            "p.createdAt) " +
        "FROM " +
            "Post p " +
        "LEFT JOIN Community c " +
            "ON p.community.communityId = c.communityId " +
        "LEFT JOIN AppUser au " +
            "ON au.userId = p.author.userId " +
        "LEFT JOIN Vote v " +
            "ON v.voteKey.postId = p.postId " +
        "LEFT JOIN VoteType vt " +
            "ON v.voteType.voteTypeId = vt.voteTypeId " +
        "WHERE " +
            "p.postId = :postId " +
        "GROUP BY " +
            "p.postId, au.username, c.communityName";

    public static final String GET_ALL_POST_DTO_BY_COMMUNITY_ID =
        "SELECT new com.goutham.redditservice.dto.PostDTO(" +
            "p.postId, " +
            "p.title, " +
            "p.content, " +
            "au.username, " +
            "c.communityName, " +
            "SUM(vt.voteTypeValue), " +
            "p.createdAt) " +
        "FROM " +
            "Post p " +
        "LEFT JOIN Community c " +
            "ON p.community.communityId = c.communityId " +
        "LEFT JOIN AppUser au " +
            "ON au.userId = p.author.userId " +
        "LEFT JOIN Vote v " +
            "ON v.voteKey.postId = p.postId " +
        "LEFT JOIN VoteType vt " +
            "ON v.voteType.voteTypeId = vt.voteTypeId " +
        "WHERE " +
            "c.communityId = :communityId " +
        "GROUP BY " +
            "p.postId, au.username, c.communityName";

    public static final String GET_ALL_POST_DTO_BY_USER_ID =
        "SELECT new com.goutham.redditservice.dto.PostDTO(" +
            "p.postId, " +
            "p.title, " +
            "p.content, " +
            "au.username, " +
            "c.communityName, " +
            "SUM(vt.voteTypeValue), " +
            "p.createdAt) " +
        "FROM " +
            "Post p " +
        "LEFT JOIN Community c " +
            "ON p.community.communityId = c.communityId " +
        "LEFT JOIN AppUser au " +
            "ON au.userId = p.author.userId " +
        "LEFT JOIN Vote v " +
            "ON v.voteKey.postId = p.postId " +
        "LEFT JOIN VoteType vt " +
            "ON v.voteType.voteTypeId = vt.voteTypeId " +
        "WHERE " +
            "au.userId = :userId " +
        "GROUP BY " +
            "p.postId, au.username, c.communityName";

    public static final String FIND_ALL_MEMBERS_OF_COMMUNITY =
        "SELECT " +
            "au " +
        "FROM " +
            "CommunityUserAssociation cua " +
        "LEFT JOIN AppUser au " +
            "ON au.userId = cua.communityUserAssociationKey.userId " +
        "WHERE " +
            "cua.communityUserAssociationKey.communityId = :communityId";

    public static final String FIND_ALL_COMMUNITIES_OF_USER =
        "SELECT " +
            "c " +
        "FROM " +
            "CommunityUserAssociation cua " +
        "LEFT JOIN Community c " +
            "ON c.communityId = cua.communityUserAssociationKey.communityId " +
        "WHERE " +
            "cua.communityUserAssociationKey.userId = :userId";
}
