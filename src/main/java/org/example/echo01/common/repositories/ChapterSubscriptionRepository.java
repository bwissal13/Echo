package org.example.echo01.common.repositories;

import org.example.echo01.common.entities.ChapterSubscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterSubscriptionRepository extends JpaRepository<ChapterSubscription, Long> {
    List<ChapterSubscription> findByChapterId(Long chapterId);
    
    List<ChapterSubscription> findByUserId(Long userId);
    
    Optional<ChapterSubscription> findByChapterIdAndUserId(Long chapterId, Long userId);
    
    boolean existsByChapterIdAndUserId(Long chapterId, Long userId);
    
    void deleteByChapterIdAndUserId(Long chapterId, Long userId);
    
    Page<ChapterSubscription> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<ChapterSubscription> findByChapterBookId(Long bookId);

    Page<ChapterSubscription> findByChapterIdOrderByCreatedAtDesc(Long chapterId, Pageable pageable);
} 