package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.entity.LikeEntity;
import org.proteam24.zeroneapplication.entity.PostEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikesRepository extends JpaRepository<LikeEntity, Long> {
    @Query(value = "select l from LikeEntity l where l.person = ?1 and l.post = ?2 and l.type = 'Post'")
    LikeEntity getPostLikeByPerson(UserEntity person, PostEntity post);

    @Query(value = "select l from LikeEntity l where l.person = ?1 and l.entityId = ?2 and l.type = 'Comment'")
    LikeEntity getCommentLikeByPerson(UserEntity person, Long commentId);

    @Query(value = "select l from LikeEntity l where l.post = ?1 AND l.type = 'Post'")
    List<LikeEntity> getPostsLike(PostEntity post);

    @Query(value = "select l from LikeEntity l where l.entityId = ?1 AND l.type = 'Comment'")
    List<LikeEntity> getCommentsLike(Long commentId);
}
