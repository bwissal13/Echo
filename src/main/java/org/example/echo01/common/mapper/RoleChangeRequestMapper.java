package org.example.echo01.common.mapper;

import org.example.echo01.common.dto.response.RoleChangeRequestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleChangeRequestMapper {
    RoleChangeRequestMapper INSTANCE = Mappers.getMapper(RoleChangeRequestMapper.class);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    RoleChangeRequestResponse toResponse(org.example.echo01.common.entities.RoleChangeRequest entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "processed", constant = "false")
    @Mapping(target = "approved", constant = "false")
    @Mapping(target = "adminComment", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    org.example.echo01.common.entities.RoleChangeRequest toEntity(org.example.echo01.common.dto.request.RoleChangeRequest dto);
} 