package org.proteam24.zeroneapplication.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.proteam24.zeroneapplication.dto.*;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.repository.NotificationSettingsRepository;
import org.proteam24.zeroneapplication.repository.UserRepository;
import org.proteam24.zeroneapplication.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;


@SpringBootTest(classes = UserServiceImpl.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Тест UserServiceImpl")
class UserServiceImplTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NotificationSettingsRepository notificationSettingsRepository;

    @MockBean
    private MailSenderImpl mailSenderImpl;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("Регистрация пользователя")
    void testRegister() {

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("email@email.com");
        BaseResponseDto<AdditionalPropDto> baseResponseDto = userService.register(userEntity);
        Mockito.verify(userRepository, Mockito.times(1))
                .save(userEntity);
        Mockito.verify(mailSenderImpl, Mockito.times(1))
                .send(
                        ArgumentMatchers.eq(userEntity.getEmail()),
                        ArgumentMatchers.eq("Вы зарегистрированы"),
                        ArgumentMatchers.contains("вы успешно зарегистрированы на сайте"));
        Assertions.assertEquals("ok", baseResponseDto.getData().getAdditionalProp1());

    }

    @Test
    @DisplayName("Регистрация пользователя отрицательный результат")
    void testRegisterFalse() {

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("email@email.com");
        Mockito.doReturn(userEntity).when(userRepository).findByEmail(userEntity.getEmail());
        BaseResponseDto<AdditionalPropDto> baseResponseDto = userService.register(userEntity);
        Assertions.assertEquals("Email занят", baseResponseDto.getError());
    }

    @Test
    @DisplayName("Поиск по email пользователя")
    void testFindByEmail() {
        String username = "email@email.com";
        userService.findByEmail(username);
        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(username);
    }

    @Test
    @DisplayName("Поиск по ID пользователя отрицательный результат")
    void testFindByIdFalse() {
        UserEntity result = userRepository.findById(1L).orElse(null);
        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("Поиск по ID пользователя")
    void testFindById() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        Optional<UserEntity> user = Optional.of(userEntity);
        Mockito.doReturn(user).when(userRepository).findById(1L);
        Optional<UserEntity> result = userRepository.findById(1L);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Восстановления доступа по email")
    void testRestore() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("old@email.com");
        Mockito.doReturn(userEntity).when(userRepository).findByEmail(userEntity.getEmail());
        BaseResponseDto<AdditionalPropDto> baseResponseDto = userService.restore("old@email.com");
        Mockito.verify(mailSenderImpl, Mockito.times(1))
                .send(
                        ArgumentMatchers.eq(userEntity.getEmail()),
                        ArgumentMatchers.eq("Activation code"),
                        ArgumentMatchers.contains("Код для восстановления"));
        Assertions.assertEquals("ok", baseResponseDto.getData().getAdditionalProp1());
    }

    @Test
    @DisplayName("Восстановления доступа новый пароль на email")
    void testRecoveryComplete() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("old@email.com");
        userEntity.setConfirmationCode("code");
        Mockito.doReturn(userEntity).when(userRepository).findByEmail(userEntity.getEmail());
        BaseResponseDto<AdditionalPropDto> baseResponseDto = userService.recoveryComplete(userEntity.getConfirmationCode(), userEntity.getEmail());
        Mockito.verify(mailSenderImpl, Mockito.times(1))
                .send(
                        ArgumentMatchers.eq(userEntity.getEmail()),
                        ArgumentMatchers.eq("New password"),
                        ArgumentMatchers.contains("Новый пароль"));
        Assertions.assertEquals("ok", baseResponseDto.getData().getAdditionalProp1());

        Mockito.verify(userRepository, Mockito.times(1))
                .save(userEntity);
    }

    @Test
    @DisplayName("Изменение пароля")
    void testChangePassword() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("old@email.com");
        userEntity.setPassword("code");
        Mockito.doReturn(userEntity.getEmail()).when(jwtTokenProvider).getUsername("code");
        Mockito.doReturn(userEntity).when(userRepository).findByEmail(userEntity.getEmail());
        BaseResponseDto<AdditionalPropDto> baseResponseDto = userService.changePassword("password", "code");
        Assertions.assertEquals("ok", baseResponseDto.getData().getAdditionalProp1());
        Mockito.verify(userRepository, Mockito.times(1))
                .save(userEntity);
    }

    @Test
    @DisplayName("Обновление профиля")
    void testUpdateProfile() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("old@email.com");
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("newName");
        Mockito.doReturn(userEntity).when(userRepository).findByEmail(userEntity.getEmail());
        userService.updateProfile(updateUserDto, userEntity.getEmail());

        BaseResponseDto<UserDto> baseResponseDto = new BaseResponseDto<>(UserDto.fromUser(userEntity));
        Assertions.assertNotNull(baseResponseDto.getData().getEmail());
        Assertions.assertEquals("newName", baseResponseDto.getData().getFirstName());
        Mockito.verify(userRepository, Mockito.times(1))
                .save(userEntity);
    }

    @Test
    @DisplayName("Авторизация")
    void testLogin() {
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setEmail("akrafit@gmail.com");
        requestDto.setPassword("WQa6J5do");
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("akrafit@gmail.com");
        Mockito.doReturn("token").when(jwtTokenProvider).createToken("akrafit@gmail.com");
        Mockito.doReturn(userEntity).when(userRepository).findByEmail(userEntity.getEmail());
        BaseResponseDto<UserDto> baseResponseDto = userService.login(requestDto);
        System.out.println(baseResponseDto.getData().getToken());
        Assertions.assertEquals(requestDto.getEmail(), baseResponseDto.getData().getEmail());
        Assertions.assertEquals("token", baseResponseDto.getData().getToken());

    }

}