package org.example.echo01.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCommentResponse {
    private Long id;
    private String content;
    private Long userId;
    private String userFullName;
    private Long bookId;
    private Long parentCommentId;
    private List<BookCommentResponse> replies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 