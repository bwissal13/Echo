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
public class BookmarkGroupResponse {
    private Long id;
    private String name;
    private List<BookResponse> books;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime modifiedAt;
    private String createdBy;
    private String updatedBy;
    private String modifiedBy;
    private Boolean deleted;
} 