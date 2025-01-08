package org.example.echo01.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo01.auth.enums.Role;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleChangeRequestResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private Role requestedRole;
    private String reason;
    private boolean approved;
    private boolean processed;
    private String adminComment;
    private LocalDateTime createdAt;
} 