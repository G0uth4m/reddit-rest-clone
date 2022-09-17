package com.goutham.redditservice.constant;

public class SqlQueries {

    public static final String GET_VOTE_COUNT_BY_POST_ID =
        "SELECT " +
            "SUM(vt.voteTypeValue) " +
        "FROM " +
            "PostVote v " +
        "LEFT JOIN VoteType vt " +
            "ON v.voteType.voteTypeId = vt.voteTypeId " +
        "WHERE " +
            "v.postVoteKey.postId = :postId";

    public static final String GET_VOTE_COUNT_BY_COMMENT_ID =
        "SELECT " +
            "SUM(vt.voteTypeValue) " +
        "FROM " +
            "CommentVote v " +
        "LEFT JOIN VoteType vt " +
            "ON v.voteType.voteTypeId = vt.voteTypeId " +
        "WHERE " +
            "v.commentVoteKey.commentId = :commentId";

    public static final String GET_POST_DTO_BY_ID =
        "SELECT new com.goutham.redditservice.dto.PostDTO(" +
            "p.postId, " +
            "p.title, " +
            "p.content, " +
            "p.author.username, " +
            "p.community.communityName, " +
            "SUM(vt.voteTypeValue), " +
            "p.createdAt) " +
        "FROM " +
            "Post p " +
        "LEFT JOIN PostVote v " +
            "ON v.postVoteKey.postId = p.postId " +
        "LEFT JOIN VoteType vt " +
            "ON v.voteType.voteTypeId = vt.voteTypeId " +
        "WHERE " +
            "p.postId = :postId " +
        "GROUP BY " +
            "p.postId, p.author.username, p.community.communityName";

    public static final String GET_ALL_POST_DTO_BY_COMMUNITY_ID =
        "SELECT new com.goutham.redditservice.dto.PostDTO(" +
            "p.postId, " +
            "p.title, " +
            "p.content, " +
            "p.author.username, " +
            "p.community.communityName, " +
            "SUM(vt.voteTypeValue), " +
            "p.createdAt) " +
        "FROM " +
            "Post p " +
        "LEFT JOIN PostVote v " +
            "ON v.postVoteKey.postId = p.postId " +
        "LEFT JOIN VoteType vt " +
            "ON v.voteType.voteTypeId = vt.voteTypeId " +
        "WHERE " +
            "p.community.communityId = :communityId " +
        "GROUP BY " +
            "p.postId, p.author.username, p.community.communityName";

    public static final String GET_ALL_POST_DTO_BY_USER_ID =
        "SELECT new com.goutham.redditservice.dto.PostDTO(" +
            "p.postId, " +
            "p.title, " +
            "p.content, " +
            "p.author.username, " +
            "p.community.communityName, " +
            "SUM(vt.voteTypeValue), " +
            "p.createdAt) " +
        "FROM " +
            "Post p " +
        "LEFT JOIN PostVote v " +
            "ON v.postVoteKey.postId = p.postId " +
        "LEFT JOIN VoteType vt " +
            "ON v.voteType.voteTypeId = vt.voteTypeId " +
        "WHERE " +
            "p.author.userId = :userId " +
        "GROUP BY " +
            "p.postId, p.author.username, p.community.communityName";

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

    public static final String GET_COMMENT_DTO_BY_ID =
        "SELECT new com.goutham.redditservice.dto.CommentDTO(" +
            "c.commentId, " +
            "c.content, " +
            "c.author.username, " +
            "SUM(vt.voteTypeValue), " +
            "c.createdAt, " +
            "c.updatedAt) " +
        "FROM " +
             "Comment c " +
        "LEFT JOIN CommentVote cv " +
            "on cv.commentVoteKey.commentId = c.commentId " +
        "LEFT JOIN VoteType vt " +
            "ON cv.voteType.voteTypeId = vt.voteTypeId " +
        "WHERE " +
            "c.commentId = :commentId " +
        "GROUP BY " +
            "c.commentId, c.author.username";

    public static final String GET_COMMENT_DTO_BY_POST_ID =
        "SELECT new com.goutham.redditservice.dto.CommentDTO(" +
            "c.commentId, " +
            "c.content, " +
            "c.author.username, " +
            "SUM(vt.voteTypeValue), " +
            "c.createdAt, " +
            "c.updatedAt) " +
        "FROM " +
            "Comment c " +
        "LEFT JOIN CommentVote cv " +
            "on cv.commentVoteKey.commentId = c.commentId " +
        "LEFT JOIN VoteType vt " +
            "ON cv.voteType.voteTypeId = vt.voteTypeId " +
        "WHERE " +
            "c.post.postId = :postId " +
        "GROUP BY " +
            "c.commentId, c.author.username";

    public static final String GET_COMMENT_DTO_BY_USER_ID =
        "SELECT new com.goutham.redditservice.dto.CommentDTO(" +
            "c.commentId, " +
            "c.content, " +
            "c.author.username, " +
            "SUM(vt.voteTypeValue), " +
            "c.createdAt, " +
            "c.updatedAt) " +
        "FROM " +
            "Comment c " +
        "LEFT JOIN CommentVote cv " +
            "on cv.commentVoteKey.commentId = c.commentId " +
        "LEFT JOIN VoteType vt " +
            "ON cv.voteType.voteTypeId = vt.voteTypeId " +
        "WHERE " +
            "c.author.userId = :userId " +
        "GROUP BY " +
            "c.commentId, c.author.username";
}
