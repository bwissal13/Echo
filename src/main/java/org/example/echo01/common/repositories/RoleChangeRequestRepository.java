package org.example.echo01.common.repositories;

import org.example.echo01.auth.entities.User;
import org.example.echo01.common.entities.RoleChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleChangeRequestRepository extends JpaRepository<RoleChangeRequest, Long> {
    List<RoleChangeRequest> findByUserAndProcessedFalse(User user);
    Optional<RoleChangeRequest> findByIdAndProcessedFalse(Long id);
    List<RoleChangeRequest> findAllByProcessedFalse();
} 