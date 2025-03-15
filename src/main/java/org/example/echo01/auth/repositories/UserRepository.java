package org.example.echo01.auth.repositories;

import org.example.echo01.auth.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Query("""
            SELECT u FROM User u 
            WHERE LOWER(u.firstname) LIKE LOWER(CONCAT('%', :search, '%')) 
            OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :search, '%')) 
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    @Query(value = """
            SELECT DISTINCT u FROM User u 
            WHERE EXISTS (SELECT 1 FROM Book b WHERE b.author = u)
            """)
    Page<User> findAuthors(Pageable pageable);

    @Query(value = """
            SELECT DISTINCT u FROM User u 
            WHERE EXISTS (SELECT 1 FROM Book b WHERE b.author = u)
            AND (LOWER(u.firstname) LIKE LOWER(CONCAT('%', :search, '%')) 
            OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :search, '%')) 
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<User> findAuthorsWithSearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT u.followers FROM User u WHERE u.id = :authorId")
    Page<User> findFollowersByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @Query("SELECT u.following FROM User u WHERE u.id = :userId")
    Page<User> findFollowingByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.followers f " +
           "WHERE f.id = :followerId AND u.id = :followedId")
    boolean existsByFollowerIdAndFollowedId(
            @Param("followerId") Long followerId, 
            @Param("followedId") Long followedId);
} 