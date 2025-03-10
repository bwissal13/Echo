package org.example.echo01.common.mappers;

import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.CreateBookRequest;
import org.example.echo01.common.dto.request.UpdateBookRequest;
import org.example.echo01.common.dto.response.BookResponse;
import org.example.echo01.common.entities.Book;
import org.example.echo01.common.dto.response.BookPreviewResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ChapterMapper.class})
public interface BookMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", source = "author")
    @Mapping(target = "chapters", ignore = true)
    @Mapping(target = "subscribers", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "permanentDeleteAt", ignore = true)
    Book toEntity(CreateBookRequest request, User author);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorFullName", expression = "java(book.getAuthor().getFirstname() + ' ' + book.getAuthor().getLastname())")
    @Mapping(target = "subscribersCount", expression = "java(book.getSubscribers().size())")
    BookResponse toResponse(Book book);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    @Mapping(target = "subscribers", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "permanentDeleteAt", ignore = true)
    void partialUpdate(UpdateBookRequest request, @MappingTarget Book book);

    @Mapping(target = "publishedAt", source = "publishedAt")
    @Mapping(target = "coverImage", source = "coverImage")
    BookPreviewResponse toPreviewResponse(Book book);
} 