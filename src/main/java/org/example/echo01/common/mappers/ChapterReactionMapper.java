package org.example.echo01.common.mappers;

import org.example.echo01.common.dto.request.ChapterReactionRequest;
import org.example.echo01.common.dto.response.ChapterReactionResponse;
import org.example.echo01.common.entities.Chapter;
import org.example.echo01.common.entities.ChapterReaction;
import org.example.echo01.auth.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChapterReactionMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chapter", source = "chapter")
    @Mapping(target = "user", source = "user")
    ChapterReaction toEntity(ChapterReactionRequest request, Chapter chapter, User user);

    @Mapping(target = "chapterId", source = "chapter.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(reaction.getUser().getFirstname() + ' ' + reaction.getUser().getLastname())")
    ChapterReactionResponse toResponse(ChapterReaction reaction);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chapter", ignore = true)
    @Mapping(target = "user", ignore = true)
    void partialUpdate(ChapterReactionRequest request, @MappingTarget ChapterReaction reaction);
} 