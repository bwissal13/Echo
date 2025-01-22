package org.example.echo01.auth.repositories;

import org.example.echo01.auth.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Query("""
            SELECT u FROM User u 
            WHERE LOWER(u.firstname) LIKE LOWER(CONCAT('%', :search, '%')) 
            OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :search, '%')) 
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);
} 