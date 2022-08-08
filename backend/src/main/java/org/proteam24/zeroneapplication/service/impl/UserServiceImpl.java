package org.proteam24.zeroneapplication.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.proteam24.zeroneapplication.dto.*;
import org.proteam24.zeroneapplication.entity.NotificationsSettingsEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.entity.enumerated.Status;
import org.proteam24.zeroneapplication.repository.NotificationSettingsRepository;
import org.proteam24.zeroneapplication.repository.UserRepository;
import org.proteam24.zeroneapplication.security.jwt.JwtTokenProvider;
import org.proteam24.zeroneapplication.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final String DEFAULT_AVATAR = "https://res.cloudinary.com/dyunvdrkg/image/upload/v1654106824/ujwhl2zxd4yxrqjm9wmi.jpg";
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final NotificationSettingsRepository notificationSettingsRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    private final MailSenderImpl mailSenderImpl;
    private final JwtTokenProvider jwtTokenProvider;
    int secretCodeLength = 4;
    boolean useLettersForSecretCode = true;
    boolean useNumbersForSecretCode = false;

    public UserServiceImpl(UserRepository userRepository,
                           AuthenticationManager authenticationManager,
                           NotificationSettingsRepository notificationSettingsRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           MailSenderImpl mailSenderImpl,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderImpl = mailSenderImpl;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public BaseResponseDto<AdditionalPropDto> register(UserEntity userEntity) {
        UserEntity userInDB = userRepository.findByEmail(userEntity.getEmail());
        if (userInDB == null) {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userEntity.setStatus(Status.ACTIVE);
            userEntity.setPhoto(DEFAULT_AVATAR);
            UserEntity registeredUserEntity = userRepository.save(userEntity);
            String message = userEntity.getFirstName() + ", вы успешно зарегистрированы на сайте.";
            mailSenderImpl.send(userEntity.getEmail(), "Вы зарегистрированы", message);
            log.info("IN register - user: {} successfully registered", registeredUserEntity);
            return new BaseResponseDto<>(new AdditionalPropDto("ok"));
        } else {
            return new BaseResponseDto<>(null, "Email занят");
        }
    }


    @Override
    public UserEntity findByEmail(String username) {
        UserEntity result = userRepository.findByEmail(username);
        log.info("IN findByEmail - user: {} found by email : {}", result, username);
        return result;
    }

    @Override
    public UserEntity findById(Long id) {
        UserEntity result = userRepository.findById(id).orElse(null);

        if (result == null) {
            log.info("IN findById - no user  found by id: {}", id);
            return null;
        }
        log.info("IN findById - user: {} found by id", result);
        return result;
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
        log.info("IN delete - user with id: {} successfully deleted", id);
    }

    public BaseResponseDto<AdditionalPropDto> restore(String eMail) throws NoSuchAlgorithmException {
        UserEntity userEntity = userRepository.findByEmail(eMail);

        if (userEntity != null) {
            userEntity.setConfirmationCode(getHashCode(generateSecretCode()));
            userRepository.save(userEntity);
            String message = "Код для восстановления " + userEntity.getConfirmationCode();
            mailSenderImpl.send(userEntity.getEmail(), "Activation code", message);
            log.info("IN restore - user with eMail: {} successfully sanded code", eMail);
            return new BaseResponseDto<>(new AdditionalPropDto("ok"));

        } else {
            log.info("IN restore - user with eMail: {} error sand code", eMail);
            return new BaseResponseDto<>(null, "error");
        }
    }

    @Override
    public BaseResponseDto<AdditionalPropDto> recoveryComplete(String key, String eMail) {
        UserEntity userEntity = userRepository.findByEmail(eMail);
        if (userEntity == null) {
            log.info("IN recoveryComplete - user with eMail: {} don't find in DataBase ", eMail);
            return new BaseResponseDto<>(null, "error");
        }
        String userKey = userEntity.getConfirmationCode();
        if (userKey.equals(key)) {
            String newPassword = RandomStringUtils.random(8, true, true);
            String message = "Новый пароль: " + newPassword;
            mailSenderImpl.send(userEntity.getEmail(), "New password", message);
            userEntity.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userEntity);
            log.info("IN recoveryComplete - user with eMail: {} successfully update password ", eMail);
            return new BaseResponseDto<>(new AdditionalPropDto("ok"));
        }
        return new BaseResponseDto<>(null, "error");
    }

    @Override
    public BaseResponseDto<AdditionalPropDto> changePassword(String newPassword, String token) {
        String eMail = jwtTokenProvider.getUsername(token);
        UserEntity userEntity = userRepository.findByEmail(eMail);
        if (userEntity == null) {
            log.info("IN changePassword - user with eMail: {} don't change password ", eMail);
            return new BaseResponseDto<>(null, "error");
        } else {
            userEntity.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userEntity);
            log.info("IN changePassword - user with eMail: {} successfully change password ", eMail);
            return new BaseResponseDto<>(new AdditionalPropDto("ok"));
        }
    }

    @Override
    public BaseResponseDto<AdditionalPropDto> changeEmail(String password, String eMail) {
        UserEntity userEntity = userRepository.findByEmail(eMail);
        if (userEntity == null) {
            log.info("IN changeEmail - user with eMail: {} don't change email ", eMail);
            return new BaseResponseDto<>(null, "error");
        } else {
            userEntity.setEmail(eMail);
            userRepository.save(userEntity);
            log.info("IN changeEmail - user with eMail: {} successfully change email ", eMail);
            return new BaseResponseDto<>(new AdditionalPropDto("ok"), "");
        }
    }

    private String generateSecretCode() {
        return RandomStringUtils.random(secretCodeLength, useLettersForSecretCode, useNumbersForSecretCode);
    }

    public String getHashCode(String password) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        for (Byte b : bytes) {
            stringBuilder.append(String.format("%02X", b));
        }
        return String.valueOf(stringBuilder);
    }

    @Override
    public BaseResponseDto<UserDto> updateProfile(UpdateUserDto userDto, String email) {

        UserEntity user = userRepository.findByEmail(email);
        UserEntity updateUser = userDto.toUser(user);
        log.info(updateUser.getCity());
        userRepository.save(updateUser);

        return new BaseResponseDto<>(UserDto.fromUser(user));
    }

    @Override
    public BaseResponseDto<UserDto> login(AuthenticationRequestDto requestDto) {
        try {
            String email = requestDto.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, requestDto.getPassword()));
            UserEntity userEntity = userRepository.findByEmail(email);
            if (userEntity == null) {
                throw new UsernameNotFoundException("User with username: " + email + " not found");
            }
            String token = jwtTokenProvider.createToken(email);
            UserDto userDto = UserDto.fromUser(userEntity);
            userDto.setToken(token);
            return new BaseResponseDto<>(userDto);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public BaseResponseDto<UserDto> getUserById(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            return new BaseResponseDto<>(null, "User with id:" + id + " not found");
        }
        UserEntity userEntity = userRepository.findById(id).get();
        UserDto userDto = UserDto.fromUser(userEntity);
        return new BaseResponseDto<>(userDto);
    }

    @Override
    public BaseResponseDto<UserDto> getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        UserDto userDto;
        try {
            if (userEntity != null) {
                userDto = UserDto.fromUser(userEntity);
                userDto.setToken("");
                return new BaseResponseDto<>(userDto);
            } else {

                return new BaseResponseDto<>(null, "Not find email");
            }
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
    @Override
    public BaseResponseDto<List<NotificationAccountDto>> getNotification(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        List<NotificationsSettingsEntity> settingList = notificationSettingsRepository.findByUser(userEntity);
        if (settingList != null) {
            List<NotificationAccountDto> list = new ArrayList<>();
            settingList.forEach(n -> {
                NotificationAccountDto notificationAccountDto = new NotificationAccountDto();
                notificationAccountDto.setType(n.getType());
                notificationAccountDto.setEnable(true);
                list.add(notificationAccountDto);

            });
            return new BaseResponseDto<>(list);
        } else {
            return new BaseResponseDto<>(null);
        }
    }

    @Override
    public BaseResponseDto<AdditionalPropDto> changeNotification(NotificationAccountDto request, String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (request.isEnable()) {
            NotificationsSettingsEntity notificationsSettings = new NotificationsSettingsEntity();
            notificationsSettings.setUser(userEntity);
            notificationsSettings.setType(request.getType());
            notificationSettingsRepository.save(notificationsSettings);
        } else {
            notificationSettingsRepository.deleteByUserAndType(userEntity, request.getType());
        }
        return new BaseResponseDto<>(new AdditionalPropDto("ok"));
    }
    @Override
    public LocalDateTime getLocalDateTimeZoneOffsetUtc() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

}