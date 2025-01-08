package org.example.echo01.auth.repositories;

import org.example.echo01.auth.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<User> findByFirstnameContainingOrLastnameContainingOrEmailContaining(
            String firstname, String lastname, String email, Pageable pageable);
} 