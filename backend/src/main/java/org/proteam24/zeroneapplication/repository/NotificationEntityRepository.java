package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.entity.NotificationEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationEntityRepository extends CrudRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findAllByPersonAndStatus(UserEntity userEntity, int status, Pageable pageable);

    @Query(value = "SELECT n FROM NotificationEntity n WHERE n.sentTime <= ?1")
    List<NotificationEntity> getNotificationsForDelete(LocalDateTime lastUpdate);


}
