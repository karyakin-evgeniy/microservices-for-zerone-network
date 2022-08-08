package org.proteam24.zeroneapplication.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.dto.BaseResponseSomeDto;
import org.proteam24.zeroneapplication.dto.NotificationDto;
import org.proteam24.zeroneapplication.dto.UserDto;
import org.proteam24.zeroneapplication.dto.socketdto.SocketNotificationDto;
import org.proteam24.zeroneapplication.dto.socketdto.SocketUserDto;
import org.proteam24.zeroneapplication.entity.*;
import org.proteam24.zeroneapplication.entity.enumerated.NotificationType;
import org.proteam24.zeroneapplication.repository.FriendshipRepository;
import org.proteam24.zeroneapplication.repository.NotificationEntityRepository;
import org.proteam24.zeroneapplication.repository.UserRepository;
import org.proteam24.zeroneapplication.service.NotificationService;
import org.proteam24.zeroneapplication.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationEntityRepository notificationEntityRepository;
    private final UserService userService;
    private final SocketIOServiceImpl socketIOService;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;


    @Value("${scheduler.delete.notification.day}")
    private Integer daysBeforeDeleted;


    //@Scheduled(cron = "*/60 * * * * *")
        @Scheduled(cron = "0 0 5 * * *")
    void checkBirthdayNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<UserEntity> allUsers = userRepository.findAllByBirthDate();
        allUsers.forEach(userEntity -> {
            List<FriendshipEntity> userFriends = friendshipRepository.findAllBySrcPersonAndStatus(userEntity);
            userFriends.forEach(friendshipEntity -> {
                NotificationEntity notification = new NotificationEntity();
                notification.setSentTime(LocalDateTime.now());
                notification.setNotificationType(String.valueOf(NotificationType.FRIEND_BIRTHDAY));
                notification.setPerson(friendshipEntity.getDstPerson());
                notification.setStatus(1);
                notificationEntityRepository.save(notification);
            });
        });
    }

    @Override
    public BaseResponseSomeDto<List<NotificationDto>> getNotifications(int offset, int itemPerPage, String email) {
        Pageable pageable = PageRequest.of(offset /= itemPerPage, itemPerPage);
        UserEntity userEntity = userService.findByEmail(email);
        Page<NotificationEntity> userNotifications = notificationEntityRepository.findAllByPersonAndStatus(userEntity, 1, pageable);


        List<NotificationDto> userNotificationDtoList = new ArrayList<>();
        userNotifications.forEach(notificationEntity -> {
            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setEntityAuthor(UserDto.fromUser(notificationEntity.getPerson()));
            notificationDto.setEntityId(notificationEntity.getId());
            notificationDto.setId(notificationEntity.getId());
            notificationDto.setSentTime(notificationEntity.getSentTime().toInstant(ZoneOffset.UTC));
            notificationDto.setEventType(String.valueOf(NotificationType.POST_COMMENT));
            userNotificationDtoList.add(notificationDto);
        });
        BaseResponseSomeDto<List<NotificationDto>> baseResponseSomeDto = new BaseResponseSomeDto();
        baseResponseSomeDto.setData(userNotificationDtoList);
        baseResponseSomeDto.setOffset(offset);
        baseResponseSomeDto.setPerPage(itemPerPage);
        baseResponseSomeDto.setTotal(Math.toIntExact(userNotifications.getTotalElements()));


        return baseResponseSomeDto;
    }

    @Override
    public BaseResponseSomeDto<List<NotificationDto>> putNotifications(int offset, int itemPerPage, String email, Long notificationId) {
        Pageable pageable = PageRequest.of(offset /= itemPerPage, itemPerPage);
        UserEntity userEntity = userService.findByEmail(email);
        Optional<NotificationEntity> notificationEntityOptional = notificationEntityRepository.findById(notificationId);

        if (notificationEntityOptional.isPresent()) {
            List<NotificationDto> userNotificationDtoList = new ArrayList<>();
            NotificationEntity notificationEntity = notificationEntityOptional.get();
            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setEntityAuthor(UserDto.fromUser(notificationEntity.getPerson()));
            notificationDto.setEntityId(notificationEntity.getId());
            notificationDto.setId(notificationEntity.getId());
            notificationDto.setStatus(0);
            notificationDto.setSentTime(notificationEntity.getSentTime().toInstant(ZoneOffset.UTC));
            notificationDto.setEventType(String.valueOf(NotificationType.POST_COMMENT));
            userNotificationDtoList.add(notificationDto);

            notificationEntity.setStatus(0);
            notificationEntityRepository.save(notificationEntity);
            BaseResponseSomeDto<List<NotificationDto>> baseResponseSomeDto = new BaseResponseSomeDto();
            baseResponseSomeDto.setData(userNotificationDtoList);
            baseResponseSomeDto.setOffset(offset);
            baseResponseSomeDto.setPerPage(itemPerPage);
            baseResponseSomeDto.setTotal(1);

            return baseResponseSomeDto;
        }
        return new BaseResponseSomeDto<>();
    }


    public void pushNotification(PostEntity postEntity, UserEntity userEntity, Long createdCommentEntityId) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setNotificationType(NotificationType.POST_COMMENT.toString());
        notificationEntity.setPerson(postEntity.getAuthor());
        notificationEntity.setSentTime(LocalDateTime.now());
        notificationEntityRepository.save(notificationEntity);
        SocketNotificationDto socketNotificationDto = new SocketNotificationDto(notificationEntity,
                userEntity,
                postEntity.getId(),
                createdCommentEntityId);
        socketIOService.pushNotificationToUser(socketNotificationDto, postEntity.getAuthor());
    }


    public void pushNotificationCommentToComment(CommentEntity commentEntity, CommentEntity createdCommentEntity) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setNotificationType(NotificationType.COMMENT_COMMENT.toString());
        notificationEntity.setPerson(commentEntity.getAuthor());
        notificationEntity.setSentTime(LocalDateTime.now());
        notificationEntityRepository.save(notificationEntity);
        SocketNotificationDto socketNotificationDto = new SocketNotificationDto(notificationEntity,
                createdCommentEntity.getAuthor(),
                commentEntity.getPostEntity().getId(),
                createdCommentEntity.getId());
        socketIOService.pushNotificationToUser(socketNotificationDto, commentEntity.getAuthor());
    }


    public void pushNotificationAddFriends(UserEntity sourceUser, UserEntity destinationUser) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setNotificationType(NotificationType.FRIEND_REQUEST.toString());
        notificationEntity.setPerson(destinationUser);
        notificationEntity.setSentTime(LocalDateTime.now());
        notificationEntityRepository.save(notificationEntity);
        SocketNotificationDto socketNotificationDto = new SocketNotificationDto();
        socketNotificationDto.setId(notificationEntity.getId());
        socketNotificationDto.setNotificationType(NotificationType.FRIEND_REQUEST.toString());
        socketNotificationDto.setSentTime(Instant.now());
        socketNotificationDto.setEntityId(sourceUser.getId());
        SocketUserDto socketUserDto = SocketUserDto.fromUser(sourceUser);
        socketNotificationDto.setEntityAuthor(socketUserDto);
        socketIOService.pushNotificationToUser(socketNotificationDto, destinationUser);
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void finalRemoveNotification() {
        LocalDateTime deleteTime = LocalDateTime.now().minusDays(daysBeforeDeleted);
        List<NotificationEntity> notifications = notificationEntityRepository.getNotificationsForDelete(deleteTime);
        notificationEntityRepository.deleteAll(notifications);
    }
}