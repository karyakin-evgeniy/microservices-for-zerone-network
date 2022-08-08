package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.entity.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagEntityRepository extends JpaRepository<TagEntity, Long> {

    @Query(value = "select t from TagEntity t where t.name like concat('%', ?1, '%')",
            countQuery = "select count(t) from TagEntity t where t.name like concat('%', ?1, '%')")
    Page<TagEntity> findByName(String name, Pageable pageable);

    TagEntity findByName(String tagName);
}