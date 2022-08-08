package org.proteam24.zeroneapplication.service;

import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.proteam24.zeroneapplication.dto.BaseResponseSomeDto;
import org.proteam24.zeroneapplication.dto.UserDto;
import org.proteam24.zeroneapplication.entity.FriendshipEntity;
import org.proteam24.zeroneapplication.entity.FriendshipStatus;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.entity.enumerated.FriendshipStatusCode;
import org.proteam24.zeroneapplication.entity.enumerated.NotificationType;
import org.proteam24.zeroneapplication.repository.FriendshipRepository;
import org.proteam24.zeroneapplication.repository.NotificationSettingsRepository;
import org.proteam24.zeroneapplication.repository.UserRepository;
import org.proteam24.zeroneapplication.service.impl.NotificationServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final NotificationServiceImpl notificationService;


    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository, NotificationSettingsRepository notificationSettingsRepository, NotificationServiceImpl notificationService) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.notificationService = notificationService;
    }

    public BaseResponseSomeDto<List<UserDto>> getFriends(Principal principal, int offset, int itemPerPage) {
        UserEntity user = userRepository.findByEmail(principal.getName());
        Pageable pageable = PageRequest.of(offset /= itemPerPage, itemPerPage);
        Page<FriendshipEntity> friendshipEntities = friendshipRepository.getUserFriendships(user, pageable);
        List<UserEntity> userFriends = new ArrayList<>();
        friendshipEntities.forEach(friendshipEntity -> userFriends.add(friendshipEntity.getDstPerson()));
        List<UserDto> result = userFriends.stream().map(UserDto::fromUser).collect(Collectors.toList());
        return new BaseResponseSomeDto<>(result, offset, itemPerPage, (int) friendshipEntities.getTotalElements(), "");
    }

    public BaseResponseSomeDto<List<UserDto>> getRecommendation(Principal principal, int offset, int itemPerPage) {
        UserEntity user = userRepository.findByEmail(principal.getName());

        //Поиск друзей пользователя
        Pageable pageable = PageRequest.of(offset /= itemPerPage, itemPerPage);
        Page<FriendshipEntity> friendshipEntities = friendshipRepository.getUserFriendships(user, pageable);
        List<UserEntity> userFriends = new ArrayList<>();
        friendshipEntities.forEach(friendshipEntity -> userFriends.add(friendshipEntity.getDstPerson()));
        //Получаем друзей у друзей пользователя
        HashSet<UserEntity> friendsForUserFriends = new HashSet<>();
        userFriends.forEach(userEntity -> {
            //Список с друзьями друга
            Page<FriendshipEntity> friendsFriendshipEntities = friendshipRepository.getUserFriendships(userEntity, pageable);
            List<UserEntity> friendFriends = new ArrayList<>();
            friendsFriendshipEntities.forEach(friendshipEntity -> friendFriends.add(friendshipEntity.getDstPerson()));
            friendsForUserFriends.addAll(friendFriends);
        });

        //Получаем рекомендованных пользователей
        List<UserDto> result = new ArrayList<>();
        Page<UserEntity> usersRecommendation = userRepository.getRecommendation(user.getCity(),
                user.getBirthDate().minusYears(2), user.getBirthDate().plusYears(2), pageable);
        log.info("In getRecommendation for user: {} found {} friends and {} recommendation users", user, friendshipEntities.getTotalElements(), usersRecommendation.getTotalElements());

        HashSet<UserEntity> userRecommendationEntities = new HashSet<>();
        userRecommendationEntities.addAll(usersRecommendation.toList());
        userRecommendationEntities.addAll(friendsForUserFriends);
        userRecommendationEntities.remove(user);
        userRecommendationEntities.removeAll(userFriends);
        if (userRecommendationEntities.size() <= 10) {
            Pageable pageable1 = PageRequest.of(0, 10 - userRecommendationEntities.size());
            userRecommendationEntities.addAll(userRepository.getLastRegisteredUsersWithLimit(pageable1).toList());
            userRecommendationEntities.remove(user);
            userRecommendationEntities.removeAll(userFriends);
        }

        userRecommendationEntities.forEach(userEntity -> result.add(UserDto.fromUser(userEntity)));

        return new BaseResponseSomeDto<>(result, offset, itemPerPage, userRecommendationEntities.size(), "");
    }

    public BaseResponseSomeDto<List<UserDto>> getFriendshipRequests(Principal principal, int offset, int itemPerPage) {
        UserEntity user = userRepository.findByEmail(principal.getName());
        Pageable pageable = PageRequest.of(offset /= itemPerPage, itemPerPage);
        Page<FriendshipEntity> friendshipEntities = friendshipRepository.getFriendshipRequests(user, pageable);
        List<UserEntity> userFriendRequests = new ArrayList<>();
        friendshipEntities.forEach(friendshipEntity -> userFriendRequests.add(friendshipEntity.getSrcPerson()));
        List<UserDto> result = userFriendRequests.stream().map(UserDto::fromUser).collect(Collectors.toList());
        return new BaseResponseSomeDto<>(result, offset, itemPerPage, (int) friendshipEntities.getTotalElements(), "");
    }

    public BaseResponseDto<UserDto> deleteFriend(Principal principal, Long id) {
        UserEntity sourceUser = userRepository.findByEmail(principal.getName());
        UserEntity destinationUser = userRepository.getById(id);
        FriendshipStatus srcFriendshipStatus = new FriendshipStatus(LocalDateTime.now(), FriendshipStatusCode.SUBSCRIBED);
        FriendshipStatus dstFriendshipStatus = new FriendshipStatus(LocalDateTime.now(), FriendshipStatusCode.DECLINED);
        FriendshipEntity srcFriendshipEntity = getFriendshipEntity(sourceUser, destinationUser);
        FriendshipEntity dstFriendshipEntity = getFriendshipEntity(destinationUser, sourceUser);

        saveFriendshipEntity(dstFriendshipEntity, dstFriendshipStatus, destinationUser, sourceUser);

        if (srcFriendshipEntity.getStatus().getCode().equals(FriendshipStatusCode.FRIEND) || srcFriendshipEntity.getStatus().getCode().equals(FriendshipStatusCode.REQUEST)) {
            srcFriendshipStatus.setCode(FriendshipStatusCode.SUBSCRIBED);
            saveFriendshipEntity(srcFriendshipEntity, srcFriendshipStatus, sourceUser, destinationUser);
        }
        return new BaseResponseDto<>(UserDto.fromUser(destinationUser));
    }

    public BaseResponseDto<UserDto> addFriend(Principal principal, Long id) {
        UserEntity sourceUser = userRepository.findByEmail(principal.getName());
        UserEntity destinationUser = userRepository.getById(id);
        FriendshipStatus srcFriendshipStatus = new FriendshipStatus(LocalDateTime.now(), null);
        FriendshipStatus dstFriendshipStatus = new FriendshipStatus(LocalDateTime.now(), null);
        FriendshipEntity srcFriendshipEntity = getFriendshipEntity(sourceUser, destinationUser);
        FriendshipEntity dstFriendshipEntity = getFriendshipEntity(destinationUser, sourceUser);

        if (srcFriendshipEntity != null) {
            if (srcFriendshipEntity.getStatus().getCode().equals(FriendshipStatusCode.REQUEST)) {
                srcFriendshipStatus.setCode(FriendshipStatusCode.FRIEND);
                dstFriendshipStatus.setCode(FriendshipStatusCode.FRIEND);
            } else if (srcFriendshipEntity.getStatus().getCode().equals(FriendshipStatusCode.BLOCKED)) {
                return new BaseResponseDto<>(UserDto.fromUser(destinationUser), "You are blocked by user");
            } else if (srcFriendshipEntity.getStatus().getCode().equals(FriendshipStatusCode.DECLINED)) {
                srcFriendshipStatus.setCode(FriendshipStatusCode.DECLINED);
                dstFriendshipStatus.setCode(FriendshipStatusCode.SUBSCRIBED);
            } else if (srcFriendshipEntity.getStatus().getCode().equals(FriendshipStatusCode.FRIEND)) {
                return new BaseResponseDto<>(UserDto.fromUser(destinationUser), "You are friends");
            } else {
                srcFriendshipStatus.setCode(FriendshipStatusCode.SUBSCRIBED);
                dstFriendshipStatus.setCode(FriendshipStatusCode.REQUEST);
            }
        } else {
            srcFriendshipStatus.setCode(FriendshipStatusCode.SUBSCRIBED);
            dstFriendshipStatus.setCode(FriendshipStatusCode.REQUEST);
        }

        saveFriendshipEntity(srcFriendshipEntity, srcFriendshipStatus, sourceUser, destinationUser);
        saveFriendshipEntity(dstFriendshipEntity, dstFriendshipStatus, destinationUser, sourceUser);

        int countByUserAndType = notificationSettingsRepository.countByUserAndType(destinationUser, NotificationType.FRIEND_REQUEST.toString());
        if (countByUserAndType > 0) {
            notificationService.pushNotificationAddFriends(sourceUser, destinationUser);
        }
        return new BaseResponseDto<>(UserDto.fromUser(destinationUser));
    }

    public BaseResponseDto<UserDto> blockFriend(Principal principal, Long id) {
        UserEntity sourceUser = userRepository.findByEmail(principal.getName());
        UserEntity destinationUser = userRepository.getById(id);
        FriendshipStatus dstFriendshipStatus = new FriendshipStatus(LocalDateTime.now(), FriendshipStatusCode.BLOCKED);
        FriendshipEntity srcFriendshipEntity = getFriendshipEntity(sourceUser, destinationUser);
        FriendshipEntity dstFriendshipEntity = getFriendshipEntity(destinationUser, sourceUser);

        if (srcFriendshipEntity != null && !srcFriendshipEntity.getStatus().getCode().equals(FriendshipStatusCode.BLOCKED)) {
            saveFriendshipEntity(srcFriendshipEntity, null, null, null);
        }
        if (dstFriendshipEntity != null && dstFriendshipEntity.getStatus().getCode().equals(FriendshipStatusCode.BLOCKED)) {
            saveFriendshipEntity(dstFriendshipEntity, null, null, null);
        } else {
            saveFriendshipEntity(dstFriendshipEntity, dstFriendshipStatus, destinationUser, sourceUser);
        }
        return new BaseResponseDto<>(UserDto.fromUser(destinationUser));
    }

    public BaseResponseDto<UserDto> unblockFriend(Principal principal, Long id) {
        UserEntity sourceUser = userRepository.findByEmail(principal.getName());
        UserEntity destinationUser = userRepository.getById(id);
        FriendshipEntity dstFriendshipEntity = getFriendshipEntity(destinationUser, sourceUser);

        saveFriendshipEntity(dstFriendshipEntity, null, null, null);
        return new BaseResponseDto<>(UserDto.fromUser(destinationUser));
    }

    private FriendshipEntity getFriendshipEntity(UserEntity dstPerson, UserEntity srcPerson) {
        return friendshipRepository
                .getFriendshipEntityByDstPersonAndSrcPerson(dstPerson, srcPerson);
    }

    private void saveFriendshipEntity(FriendshipEntity entity, FriendshipStatus status, UserEntity dstPerson, UserEntity srcPerson) {
        if (entity != null) {
            friendshipRepository.deleteById(entity.getId());
        }
        if (status != null) {
            entity = new FriendshipEntity();
            entity.setStatus(status);
            entity.setDstPerson(dstPerson);
            entity.setSrcPerson(srcPerson);
            friendshipRepository.save(entity);
        }
    }


}
