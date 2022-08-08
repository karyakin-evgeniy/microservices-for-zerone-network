package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.entity.NotificationsSettingsEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface NotificationSettingsRepository extends JpaRepository<NotificationsSettingsEntity, Long> {

    int countByUserAndType(UserEntity user, @Param("type") String type);

    @Modifying
    @Transactional
    void deleteByUserAndType(UserEntity user, String type);

    List<NotificationsSettingsEntity> findByUser(UserEntity user);


}
