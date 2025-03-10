package com.readify.api.follower.repository;

import com.readify.api.follower.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {
    Integer countByAuthorId(Long authorId);
} 