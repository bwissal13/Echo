package org.example.echo01.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterRequest {
    @NotBlank(message = "Title cannot be empty")
    private String title;
    
    @NotBlank(message = "Content cannot be empty")
    private String content;
    
    @NotNull(message = "Book ID is required")
    private Long bookId;
    
    private Integer orderNumber;
} 