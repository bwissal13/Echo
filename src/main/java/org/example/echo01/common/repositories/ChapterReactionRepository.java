package org.example.echo01.common.repositories;

import org.example.echo01.common.entities.ChapterReaction;
import org.example.echo01.common.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ChapterReactionRepository extends JpaRepository<ChapterReaction, Long> {
    List<ChapterReaction> findByChapterId(Long chapterId);
    
    Optional<ChapterReaction> findByChapterIdAndUserId(Long chapterId, Long userId);
    
    @Query("SELECT r.type, COUNT(r) FROM ChapterReaction r WHERE r.chapter.id = :chapterId GROUP BY r.type")
    List<Object[]> countReactionsByType(@Param("chapterId") Long chapterId);
    
    boolean existsByChapterIdAndUserId(Long chapterId, Long userId);
    
    void deleteByChapterIdAndUserId(Long chapterId, Long userId);
} 