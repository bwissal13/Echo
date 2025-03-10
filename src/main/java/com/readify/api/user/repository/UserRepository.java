package com.readify.api.user.repository;

import com.readify.api.user.entity.User;
import com.readify.api.user.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Page<User> findAllByRoleAndEnabled(Role role, boolean enabled, Pageable pageable);
} 