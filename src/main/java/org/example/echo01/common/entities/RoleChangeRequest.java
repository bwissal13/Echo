package org.example.echo01.common.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.enums.Role;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_change_requests")
@EqualsAndHashCode(callSuper = true)
public class RoleChangeRequest extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role requestedRole;
    
    private String reason;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean approved = false;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean processed = false;
    
    private String adminComment;
} 