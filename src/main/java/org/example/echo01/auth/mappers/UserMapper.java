package org.example.echo01.auth.mappers;

import org.example.echo01.auth.dto.response.UserResponse;
import org.example.echo01.auth.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "followersCount", expression = "java(user.getFollowers().size())")
    @Mapping(target = "followingCount", expression = "java(user.getFollowing().size())")
    UserResponse toResponse(User user);
} 