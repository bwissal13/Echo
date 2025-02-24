package org.example.echo01.common.mappers;

import org.example.echo01.common.dto.request.ChapterRequest;
import org.example.echo01.common.dto.response.ChapterResponse;
import org.example.echo01.common.entities.Book;
import org.example.echo01.common.entities.Chapter;
import org.example.echo01.common.enums.ReactionType;
import org.mapstruct.*;

import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ChapterCommentMapper.class})
public interface ChapterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", source = "book")
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "subscribers", ignore = true)
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "content", source = "request.content")
    @Mapping(target = "orderNumber", source = "request.orderNumber")
    Chapter toEntity(ChapterRequest request, Book book);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "reactions", expression = "java(mapReactions(chapter))")
    @Mapping(target = "subscribersCount", expression = "java(chapter.getSubscribers().size())")
    ChapterResponse toResponse(Chapter chapter);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "subscribers", ignore = true)
    void partialUpdate(ChapterRequest request, @MappingTarget Chapter chapter);

    default Map<String, Integer> mapReactions(Chapter chapter) {
        return chapter.getReactions().stream()
            .collect(Collectors.groupingBy(
                reaction -> reaction.getType().name(),
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
    }
} 