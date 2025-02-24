package org.example.echo01.common.repositories;

import org.example.echo01.common.entities.BookSubscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookSubscriptionRepository extends JpaRepository<BookSubscription, Long> {
    List<BookSubscription> findByBookId(Long bookId);
    
    List<BookSubscription> findByUserId(Long userId);
    
    Optional<BookSubscription> findByBookIdAndUserId(Long bookId, Long userId);
    
    boolean existsByBookIdAndUserId(Long bookId, Long userId);
    
    void deleteByBookIdAndUserId(Long bookId, Long userId);
    
    Page<BookSubscription> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    Page<BookSubscription> findByBookIdOrderByCreatedAtDesc(Long bookId, Pageable pageable);
} 