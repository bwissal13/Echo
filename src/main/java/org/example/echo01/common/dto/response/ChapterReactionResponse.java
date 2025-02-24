package org.example.echo01.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo01.common.enums.ReactionType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterReactionResponse {
    private Long id;
    private ReactionType type;
    private Long chapterId;
    private Long userId;
    private String userFullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 