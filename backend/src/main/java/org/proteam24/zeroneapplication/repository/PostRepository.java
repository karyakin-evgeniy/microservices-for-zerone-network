package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.entity.PostEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query(value = "SELECT p FROM PostEntity p WHERE p.isBlocked = false AND p.isDeleted = false ORDER BY p.time DESC")
    Page<PostEntity> getSomePosts(Pageable pageable);

    @Query(value = "SELECT p FROM PostEntity p WHERE p.author = ?1 ORDER BY p.time DESC")
    Page<PostEntity> getMyPosts(UserEntity userEntity, Pageable pageable);

    @Query(value = "SELECT p FROM PostEntity p WHERE p.author = ?1 " +
            "AND p.isDeleted = false AND p.isBlocked = false " +
            "AND p.time <= ?2 ORDER BY p.time DESC")
    Page<PostEntity> getUserPosts(UserEntity userEntity, LocalDateTime time, Pageable pageable);

    @Query(value = "SELECT p FROM PostEntity p WHERE p.isDeleted = true AND p.updated <= ?1")
    List<PostEntity> getPostsForDelete(LocalDateTime lastUpdate);
}
