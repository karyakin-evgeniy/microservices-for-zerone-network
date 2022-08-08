package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    UserEntity findByEmail(String name);

    @Query(value = "SELECT u FROM UserEntity u WHERE u.city = :city AND u.birthDate BETWEEN :startDateBirthday AND :finishDateBirthday")
    Page<UserEntity> getRecommendation(String city, LocalDateTime startDateBirthday, LocalDateTime finishDateBirthday, Pageable pageable);

    @Query(value = "SELECT u FROM UserEntity u ORDER BY u.id DESC")
    Page<UserEntity> getLastRegisteredUsersWithLimit(Pageable pageable);

    @Query(value = "SELECT u FROM UserEntity u where month(u.birthDate) = month(current_date) and day(u.birthDate) = day(current_date)")
    List<UserEntity> findAllByBirthDate();
}
