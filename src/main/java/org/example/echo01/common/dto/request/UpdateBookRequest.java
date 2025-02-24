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
public class UpdateBookRequest {
    private String title;
    private String description;
    private Genre genre;
    private Boolean isPublic;
    private Boolean notifyOnNewChapter;
    private Boolean notifyOnChapterUpdate;
    private Boolean notifyOnNewComment;
} 