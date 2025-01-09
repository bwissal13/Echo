package org.example.echo01.auth.repositories;

import org.example.echo01.auth.entities.OTP;
import org.example.echo01.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findFirstByEmailAndCodeAndUsedFalseOrderByCreatedAtDesc(String email, String code);
    
    Optional<OTP> findFirstByEmailOrderByCreatedAtDesc(String email);
    
    void deleteAllByUser(User user);
} 