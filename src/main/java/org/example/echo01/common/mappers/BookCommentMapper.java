package org.example.echo01.common.mappers;

import org.example.echo01.common.dto.response.BookCommentResponse;
import org.example.echo01.common.entities.BookComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookCommentMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(comment.getUser().getFirstname() + ' ' + comment.getUser().getLastname())")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    @Mapping(target = "replies", ignore = true)
    BookCommentResponse toResponse(BookComment comment);
} 