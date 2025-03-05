package org.example.echo01.common.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddToBookmarkRequest {
    @NotNull(message = "Book ID is required")
    private Long bookId;
    
    @NotNull(message = "Group ID is required")
    private Long groupId;
} 