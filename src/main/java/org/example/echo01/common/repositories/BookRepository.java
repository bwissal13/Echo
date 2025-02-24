package org.example.echo01.common.repositories;

import org.example.echo01.common.entities.Book;
import org.example.echo01.common.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(attributePaths = {"chapters"})
    Page<Book> findByAuthorId(Long authorId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"chapters"})
    Page<Book> findByIsPublicTrue(Pageable pageable);
    
    @EntityGraph(attributePaths = {"chapters"})
    Page<Book> findByGenreAndIsPublicTrue(Genre genre, Pageable pageable);
    
    @EntityGraph(attributePaths = {"chapters"})
    @Query("SELECT b FROM Book b WHERE b.isPublic = true AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Book> searchPublicBooks(@Param("query") String query, Pageable pageable);
    
    @EntityGraph(attributePaths = {"chapters"})
    @Query("SELECT b FROM Book b WHERE b.author.id = :authorId AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Book> searchAuthorBooks(@Param("authorId") Long authorId, @Param("query") String query, Pageable pageable);
    
    @EntityGraph(attributePaths = {"chapters"})
    @Query("SELECT b FROM Book b WHERE b.id = :id AND b.author.id = :authorId")
    Optional<Book> findByIdAndAuthorId(@Param("id") Long id, @Param("authorId") Long authorId);
    
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Book b WHERE b.id = :id AND b.author.id = :authorId")
    boolean existsByIdAndAuthorId(@Param("id") Long id, @Param("authorId") Long authorId);

    // Soft delete methods
    @EntityGraph(attributePaths = {"chapters"})
    @Query("SELECT b FROM Book b WHERE b.deleted = true AND b.author.id = :authorId")
    Page<Book> findDeletedByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @EntityGraph(attributePaths = {"chapters"})
    @Query("SELECT b FROM Book b WHERE b.id = :id AND b.author.id = :authorId AND b.deleted = true")
    Optional<Book> findDeletedByIdAndAuthorId(@Param("id") Long id, @Param("authorId") Long authorId);

    @Modifying
    @Query("UPDATE Book b SET b.deleted = true, b.deletedAt = CURRENT_TIMESTAMP, " +
           "b.permanentDeleteAt = FUNCTION('TIMESTAMPADD', YEAR, 5, CURRENT_TIMESTAMP) WHERE b.id = :id")
    void softDelete(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Book b SET b.deleted = false, b.deletedAt = null, b.permanentDeleteAt = null WHERE b.id = :id")
    void restore(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Book b WHERE b.id = :id AND b.author.id = :authorId AND b.deleted = true")
    boolean existsDeletedByIdAndAuthorId(@Param("id") Long id, @Param("authorId") Long authorId);

    @Modifying
    int deleteByDeletedTrueAndPermanentDeleteAtBefore(LocalDateTime dateTime);

    @Modifying
    @Query("UPDATE Book b SET b.isPublic = true WHERE b.id = :id AND b.author.id = :authorId")
    void makePublic(@Param("id") Long id, @Param("authorId") Long authorId);

    @Modifying
    @Query("UPDATE Book b SET b.isPublic = false WHERE b.id = :id AND b.author.id = :authorId")
    void makePrivate(@Param("id") Long id, @Param("authorId") Long authorId);

    @EntityGraph(attributePaths = {"chapters"})
    @Override
    Optional<Book> findById(Long id);
} 