package org.example.echo01.common.mappers;

import org.example.echo01.common.dto.request.BookSubscriptionRequest;
import org.example.echo01.common.dto.response.SubscriptionResponse;
import org.example.echo01.common.entities.Book;
import org.example.echo01.common.entities.BookSubscription;
import org.example.echo01.auth.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookSubscriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", source = "book")
    @Mapping(target = "user", source = "user")
    BookSubscription toEntity(BookSubscriptionRequest request, Book book, User user);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(subscription.getUser().getFirstname() + ' ' + subscription.getUser().getLastname())")
    SubscriptionResponse toResponse(BookSubscription subscription);
} 