package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.entity.DialogEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DialogEntityRepository extends JpaRepository<DialogEntity, Long> {

    @Query(value = "select d from DialogEntity d where d.userFirst = :userF and d.userSecond = :userS")
    DialogEntity findDialog(UserEntity userF, UserEntity userS);
}