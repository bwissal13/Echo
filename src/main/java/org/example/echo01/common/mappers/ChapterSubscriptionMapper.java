package org.example.echo01.common.mappers;

import org.example.echo01.common.dto.request.ChapterSubscriptionRequest;
import org.example.echo01.common.dto.response.SubscriptionResponse;
import org.example.echo01.common.entities.Chapter;
import org.example.echo01.common.entities.ChapterSubscription;
import org.example.echo01.auth.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChapterSubscriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chapter", source = "chapter")
    @Mapping(target = "user", source = "user")
    ChapterSubscription toEntity(ChapterSubscriptionRequest request, Chapter chapter, User user);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(subscription.getUser().getFirstname() + ' ' + subscription.getUser().getLastname())")
    SubscriptionResponse toResponse(ChapterSubscription subscription);
} 