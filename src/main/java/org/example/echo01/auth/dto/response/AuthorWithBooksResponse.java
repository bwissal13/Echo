package org.example.echo01.auth.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.echo01.common.dto.response.BookPreviewResponse;

import java.util.List;

@Data
@Builder
public class AuthorWithBooksResponse {
    private Long id;
    private String firstname;
    private String lastname;
    private String profilePicture;
    private Integer totalBooks;
    private Integer totalFollowers;
    private List<BookPreviewResponse> books;
} 