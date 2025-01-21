package org.example.echo01.common.repositories;

import org.example.echo01.common.entities.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Page<Chapter> findByBookId(Long bookId, Pageable pageable);
} 