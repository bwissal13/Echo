package com.readify.api.book.repository;

import com.readify.api.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Integer countByAuthorId(Long authorId);
} 