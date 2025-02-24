package org.example.echo01.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo01.common.enums.Genre;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    @NotBlank(message = "Title cannot be empty")
    private String title;
    
    private String description;
    
    private Genre genre;
    
    private boolean isPublic;
    
    @Builder.Default
    private boolean notifyOnNewChapter = true;

    @Builder.Default
    private boolean notifyOnChapterUpdate = true;

    @Builder.Default
    private boolean notifyOnNewComment = true;
} 