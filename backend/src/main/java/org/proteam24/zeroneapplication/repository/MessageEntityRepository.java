package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.entity.DialogEntity;
import org.proteam24.zeroneapplication.entity.MessageEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageEntityRepository extends JpaRepository<MessageEntity, Long> {
    @Query(value = "select m.dialog from MessageEntity m where m.author = :user or m.recipient = :user group by m.dialog order by m.time desc")
    Page<DialogEntity> findAllDialogs(UserEntity user, Pageable pageable);

    @Query(value = "select m from MessageEntity m where m.dialog = :dialog and m.time = (select max(m.time) from MessageEntity m where m.dialog = :dialog)")
    MessageEntity findLastMessageDialog(DialogEntity dialog);

    @Query(value = "select count(m) from MessageEntity m where m.recipient = :user and m.readStatus = 0 and m.dialog = :dialog")
    Long countUnRead(UserEntity user, DialogEntity dialog);

    @Query(value = "select m.id from MessageEntity m where m.author = :user or m.recipient = :user group by m.dialog")
    List<Long> countAllDialogs(UserEntity user);

    @Query(value = "select m.id from MessageEntity m where m.recipient = :user and m.readStatus = 0")
    List<Long> countAllUnReaded(UserEntity user);

    @Query(value = "select m from MessageEntity m where m.dialog = :dialog order by m.time desc")
    Page<MessageEntity> findAllMessages(DialogEntity dialog, Pageable pageable);

    @Query(value = "select count(m) from MessageEntity m where m.dialog = :dialog")
    Long countAllDialogMessages(DialogEntity dialog);

    @Transactional
    @Modifying
    @Query(value = "UPDATE MessageEntity SET readStatus = 1 where recipient = :user and dialog = :dialog")
    void messageViewed(UserEntity user, DialogEntity dialog);
}