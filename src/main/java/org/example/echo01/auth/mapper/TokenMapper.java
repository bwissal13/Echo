package org.example.echo01.auth.mapper;

import org.example.echo01.auth.dto.response.TokenResponse;
import org.example.echo01.auth.entities.Token;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    TokenResponse toResponse(Token token);
} 