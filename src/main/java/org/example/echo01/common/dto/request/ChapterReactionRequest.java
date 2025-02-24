package org.example.echo01.common.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo01.common.enums.ReactionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterReactionRequest {
    @NotNull(message = "Chapter ID is required")
    private Long chapterId;
    
    @NotNull(message = "Reaction type is required")
    private ReactionType type;
} 