package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.entity.CommentEntity;
import org.proteam24.zeroneapplication.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.xml.crypto.dsig.keyinfo.PGPData;
import java.util.List;
import java.util.Optional;

public interface CommentsRepository extends JpaRepository<CommentEntity, Long> {
    @Query(value = "SELECT c FROM CommentEntity c WHERE c.postEntity = ?1 ORDER BY id")
    Page<CommentEntity> findByPostEntity(PostEntity postEntity, Pageable pageable);

    List<CommentEntity> findByParentId(Long id);

    @Query(value = "SELECT c FROM CommentEntity c WHERE c.postEntity = ?1")
    List<CommentEntity> findByPostEntityForDelete(PostEntity postEntity);
}
