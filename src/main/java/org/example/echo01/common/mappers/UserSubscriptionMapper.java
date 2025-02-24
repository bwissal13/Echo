package org.example.echo01.common.mappers;

import org.example.echo01.common.dtos.UserSubscriptionDto;
import org.example.echo01.common.entities.UserSubscription;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSubscriptionMapper {
    UserSubscriptionDto toDto(UserSubscription subscription);
} 