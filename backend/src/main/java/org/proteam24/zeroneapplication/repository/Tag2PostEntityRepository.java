package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.entity.PostEntity;
import org.proteam24.zeroneapplication.entity.Tag2PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface Tag2PostEntityRepository extends JpaRepository<Tag2PostEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "delete from Tag2PostEntity t where t.post = :post")
    void deleteTag2PostWithPostId(PostEntity post);
}