package org.example.echo01.common.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo01.auth.enums.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleChangeRequest {
    @NotNull(message = "Requested role is required")
    private Role requestedRole;
    
    private String reason;
} 