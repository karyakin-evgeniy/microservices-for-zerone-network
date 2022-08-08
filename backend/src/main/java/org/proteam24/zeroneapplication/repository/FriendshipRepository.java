package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.entity.FriendshipEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
    @Query(value = "SELECT f FROM FriendshipEntity f WHERE f.srcPerson = :user AND f.status.code = 'FRIEND'")
    Page<FriendshipEntity> getUserFriendships(UserEntity user, Pageable pageable);

    @Query(value = "SELECT u FROM FriendshipEntity u WHERE u.dstPerson = :user AND u.status.code = 'REQUEST'")
    Page<FriendshipEntity> getFriendshipRequests(UserEntity user, Pageable pageable);

    FriendshipEntity getFriendshipEntityByDstPersonAndSrcPerson(UserEntity dstPerson, UserEntity srcPerson);

    @Query(value = "SELECT f FROM FriendshipEntity f WHERE f.srcPerson = :user AND f.status.code = 'FRIEND'")
    List<FriendshipEntity> findAllBySrcPersonAndStatus(UserEntity user);
}
