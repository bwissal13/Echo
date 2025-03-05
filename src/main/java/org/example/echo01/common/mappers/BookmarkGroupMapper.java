package org.example.echo01.common.mappers;

import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.CreateBookmarkGroupRequest;
import org.example.echo01.common.dto.response.BookmarkGroupResponse;
import org.example.echo01.common.entities.BookmarkGroup;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface BookmarkGroupMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "createdBy", expression = "java(user.getEmail())")
    @Mapping(target = "updatedBy", expression = "java(user.getEmail())")
    @Mapping(target = "modifiedBy", expression = "java(user.getEmail())")
    @Mapping(target = "deleted", constant = "false")
    BookmarkGroup toEntity(CreateBookmarkGroupRequest request, User user);

    @Mapping(target = "books", source = "books")
    BookmarkGroupResponse toResponse(BookmarkGroup bookmarkGroup);
} 