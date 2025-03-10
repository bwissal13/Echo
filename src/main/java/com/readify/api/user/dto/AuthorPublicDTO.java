package com.readify.api.user.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class AuthorPublicDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String bio;
    private String profilePicture;
    private Integer totalBooks;
    private Integer totalFollowers;
} 