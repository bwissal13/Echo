package org.example.echo01.common.repositories;

import org.example.echo01.common.entities.BookmarkGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkGroupRepository extends JpaRepository<BookmarkGroup, Long> {
    Page<BookmarkGroup> findByUserId(Long userId, Pageable pageable);
    boolean existsByIdAndUserId(Long id, Long userId);
} 