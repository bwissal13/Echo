package org.example.echo01.common.repositories;

import org.example.echo01.auth.entities.User;
import org.example.echo01.common.entities.UserSubscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findByTargetUserAndSubscriber(User targetUser, User subscriber);
    Page<UserSubscription> findBySubscriber(User subscriber, Pageable pageable);
    Page<UserSubscription> findByTargetUser(User targetUser, Pageable pageable);
    boolean existsByTargetUserAndSubscriber(User targetUser, User subscriber);
} 