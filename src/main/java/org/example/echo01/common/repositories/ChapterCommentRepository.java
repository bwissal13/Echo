package org.example.echo01.common.repositories;

import org.example.echo01.common.entities.ChapterComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterCommentRepository extends JpaRepository<ChapterComment, Long> {
    List<ChapterComment> findByChapterId(Long chapterId);
    List<ChapterComment> findByChapterIdAndParentCommentIsNull(Long chapterId);
    List<ChapterComment> findByParentCommentId(Long parentCommentId);
} 