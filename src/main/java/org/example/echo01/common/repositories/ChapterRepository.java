package org.example.echo01.common.repositories;

import org.example.echo01.common.entities.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Page<Chapter> findByBookIdOrderByOrderNumberAsc(Long bookId, Pageable pageable);
    
    List<Chapter> findByBookIdOrderByOrderNumberAsc(Long bookId);
    
    Optional<Chapter> findByIdAndBookId(Long id, Long bookId);
    
    @Query("SELECT c FROM Chapter c WHERE c.book.id = :bookId AND " +
           "LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Chapter> searchInBook(@Param("bookId") Long bookId, @Param("query") String query, Pageable pageable);
    
    @Query("SELECT c FROM Chapter c WHERE c.book.author.id = :authorId AND " +
           "LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Chapter> searchAuthorChapters(@Param("authorId") Long authorId, @Param("query") String query, Pageable pageable);
    
    boolean existsByIdAndBookId(Long id, Long bookId);
    
    @Query("SELECT MAX(c.orderNumber) FROM Chapter c WHERE c.book.id = :bookId")
    Integer findMaxOrderNumberByBookId(@Param("bookId") Long bookId);

    // Soft delete methods
    @Query("SELECT c FROM Chapter c WHERE c.deleted = true AND c.book.author.id = :authorId")
    Page<Chapter> findDeletedByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @Query("SELECT c FROM Chapter c WHERE c.deleted = true AND c.book.id = :bookId")
    Page<Chapter> findDeletedByBookId(@Param("bookId") Long bookId, Pageable pageable);

    @Modifying
    @Query("UPDATE Chapter c SET c.deleted = true, c.deletedAt = CURRENT_TIMESTAMP, " +
           "c.permanentDeleteAt = FUNCTION('TIMESTAMPADD', YEAR, 5, CURRENT_TIMESTAMP) WHERE c.id = :id")
    void softDelete(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Chapter c SET c.deleted = false, c.deletedAt = null, c.permanentDeleteAt = null WHERE c.id = :id")
    void restore(@Param("id") Long id);

    @Modifying
    int deleteByDeletedTrueAndPermanentDeleteAtBefore(LocalDateTime dateTime);
} 