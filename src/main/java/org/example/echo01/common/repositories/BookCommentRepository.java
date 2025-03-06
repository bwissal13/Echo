package org.example.echo01.common.repositories;

import org.example.echo01.common.entities.BookComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCommentRepository extends JpaRepository<BookComment, Long> {
    List<BookComment> findByBookId(Long bookId);
    List<BookComment> findByBookIdAndParentCommentIsNull(Long bookId);
    List<BookComment> findByParentCommentId(Long parentCommentId);
} 