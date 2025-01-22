package org.example.echo01.auth.mapper;

import org.example.echo01.auth.dto.response.RefreshTokenResponse;
import org.example.echo01.auth.entities.RefreshToken;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    RefreshTokenResponse toResponse(RefreshToken refreshToken);
} 