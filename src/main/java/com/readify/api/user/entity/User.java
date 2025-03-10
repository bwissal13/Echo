package com.readify.api.user.entity;

import com.readify.api.user.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String bio;
    
    @Column(length = 1000)
    private String profilePicture;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    private boolean enabled;
} 