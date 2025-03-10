package org.example.echo01.common.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookPreviewResponse {
    private Long id;
    private String title;
    private LocalDateTime publishedAt;
    private String coverImage;
} 