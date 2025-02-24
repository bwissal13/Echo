package org.example.echo01.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterResponse {
    private Long id;
    private String title;
    private String content;
    private Long bookId;
    private Integer orderNumber;
    private List<ChapterCommentResponse> comments;
    private Map<String, Integer> reactions; // ReactionType -> Count
    private int subscribersCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 