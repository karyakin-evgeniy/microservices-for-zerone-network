package org.proteam24.zeroneapplication.service;

import org.proteam24.zeroneapplication.dto.*;
import org.proteam24.zeroneapplication.entity.UserEntity;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    BaseResponseDto<AdditionalPropDto> register(UserEntity userEntity);

    UserEntity findByEmail(String email);

    UserEntity findById(Long id);

    void delete(Long id);

    BaseResponseDto<AdditionalPropDto> restore(String email) throws NoSuchAlgorithmException;

    BaseResponseDto<AdditionalPropDto> recoveryComplete(String key, String email);

    BaseResponseDto<AdditionalPropDto> changePassword(String password, String token);

    BaseResponseDto<AdditionalPropDto> changeEmail(String password, String name);

    BaseResponseDto<UserDto> updateProfile(UpdateUserDto userDto, String name);

    BaseResponseDto<UserDto> login(AuthenticationRequestDto requestDto);

    BaseResponseDto<UserDto> getUserById(Long id);

    BaseResponseDto<UserDto> getUser(String name);

    BaseResponseDto<List<NotificationAccountDto>> getNotification(String email);

    BaseResponseDto<AdditionalPropDto> changeNotification(NotificationAccountDto request, String email);

    LocalDateTime getLocalDateTimeZoneOffsetUtc();
}
