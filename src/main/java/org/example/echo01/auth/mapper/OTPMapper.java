package org.example.echo01.auth.mapper;

import org.example.echo01.auth.dto.response.OTPResponse;
import org.example.echo01.auth.entities.OTP;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OTPMapper {
    
    @Mapping(target = "userId", source = "user.id")
    OTPResponse toResponse(OTP otp);
} 