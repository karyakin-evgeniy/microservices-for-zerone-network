package org.proteam24.zeroneapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.proteam24.zeroneapplication.dto.*;
import org.proteam24.zeroneapplication.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/account")
@Tag(name = "Учетные записи", description = "Работа с учетными записями")
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {

        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", description = "Создание новой учётной записи")
    public BaseResponseDto<AdditionalPropDto> registration(@RequestBody RegistrationUserDto registrationUserDto) {

        return userService.register(registrationUserDto.toUser());

    }

    @PutMapping("/recovery")
    @Operation(summary = "Восстановление пароля", description = "Запрос на восстановление пароля")
    public BaseResponseDto<AdditionalPropDto> recovery(@RequestBody UsernamePasswordRecoveryDto usernamePasswordRecoveryDto) throws NoSuchAlgorithmException {

        return userService.restore(usernamePasswordRecoveryDto.getEmail());
    }

    @GetMapping("/recovery_complete")
    @ResponseBody
    @Operation(summary = "Подтверждение восстановления пароля", description = "Получение подтверждения пароля")
    public BaseResponseDto<AdditionalPropDto> recoveryComplete(@RequestParam(name = "key") String key, @RequestParam(name = "eMail") String email) {

        return userService.recoveryComplete(key, email);
    }

    @PutMapping("/password/set")
    @Operation(security = @SecurityRequirement(name = "bearer-key"),
            summary = "Смена пароля", description = "Запроля на смену пароля")
    public BaseResponseDto<AdditionalPropDto> passwordSet(@RequestBody UsernamePasswordRecoveryDto usernamePasswordRecoveryDto){

        return userService.changePassword(usernamePasswordRecoveryDto.getPassword(), usernamePasswordRecoveryDto.getToken());
    }

    @Operation(security = @SecurityRequirement(name = "bearer-key"),
            summary = "Смена email", description = "Запрос на смену email")
    @PutMapping("/email")
    public BaseResponseDto<AdditionalPropDto> emailSet(@RequestBody UsernamePasswordRecoveryDto usernamePasswordRecoveryDto, Principal principal) throws NoSuchAlgorithmException {

        if (principal != null) {
            return userService.changeEmail(usernamePasswordRecoveryDto.getPassword(), principal.getName());
        }
        return new BaseResponseDto<>(null, "Ошибка авторизации");
    }

    @GetMapping("/notifications")
    public BaseResponseDto<List<NotificationAccountDto>> getNotifications(Principal principal) {
        return userService.getNotification(principal.getName());
    }


    @Operation(security = @SecurityRequirement(name = "bearer-key"),
            summary = "Уведомления", description = "Получение уведомлений")
    @PutMapping(value = "/notifications")
    public BaseResponseDto<AdditionalPropDto> changeNotifications(@RequestBody NotificationAccountDto request,
                                                                  Principal principal) {
        if (principal != null) {
            return userService.changeNotification(request, principal.getName());
        }
        return new BaseResponseDto<>(null, "");
    }

}
