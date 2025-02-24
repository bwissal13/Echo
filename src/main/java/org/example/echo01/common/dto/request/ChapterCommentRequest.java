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
public class ChapterCommentRequest {
    @NotBlank(message = "Comment content cannot be empty")
    private String content;
    
    @NotNull(message = "Chapter ID is required")
    private Long chapterId;
    
    private Long parentCommentId;
} 