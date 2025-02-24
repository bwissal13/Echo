package org.example.echo01.common.mappers;

import org.example.echo01.common.dto.request.ChapterCommentRequest;
import org.example.echo01.common.dto.response.ChapterCommentResponse;
import org.example.echo01.common.entities.Chapter;
import org.example.echo01.common.entities.ChapterComment;
import org.example.echo01.auth.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChapterCommentMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chapter", source = "chapter")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "parentComment", source = "parentComment")
    @Mapping(target = "content", source = "request.content")
    ChapterComment toEntity(ChapterCommentRequest request, Chapter chapter, User user, ChapterComment parentComment);

    @Mapping(target = "chapterId", source = "chapter.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(comment.getUser().getFirstname() + ' ' + comment.getUser().getLastname())")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    ChapterCommentResponse toResponse(ChapterComment comment);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chapter", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    void partialUpdate(ChapterCommentRequest request, @MappingTarget ChapterComment comment);
} 