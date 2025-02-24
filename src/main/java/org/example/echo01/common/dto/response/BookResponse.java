package org.example.echo01.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo01.common.enums.Genre;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String description;
    private Genre genre;
    private Long authorId;
    private String authorFullName;
    private boolean isPublic;
    private int views;
    private List<ChapterResponse> chapters;
    private int subscribersCount;
    private boolean notifyOnNewChapter;
    private boolean notifyOnChapterUpdate;
    private boolean notifyOnNewComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 