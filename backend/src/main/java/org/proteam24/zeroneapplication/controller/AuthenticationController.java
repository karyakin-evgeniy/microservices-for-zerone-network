package org.proteam24.zeroneapplication.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.proteam24.zeroneapplication.dto.AdditionalPropDto;
import org.proteam24.zeroneapplication.dto.AuthenticationRequestDto;
import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.proteam24.zeroneapplication.dto.UserDto;
import org.proteam24.zeroneapplication.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/auth")
@Tag(name = "Авторизация", description = "Работа с авторизацией")
public class AuthenticationController {

    private final UserServiceImpl userService;

    public AuthenticationController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "Авторизация", description = "Получение токена")
    public BaseResponseDto<UserDto> login(@RequestBody AuthenticationRequestDto requestDto) {
        return userService.login(requestDto);
    }

    @GetMapping("/logout")
    @Operation(summary = "Подтверждение выхода", description = "Подтверждение выхода с учётной записи")
    public BaseResponseDto<AdditionalPropDto> logoutGet() {

        return new BaseResponseDto<>(new AdditionalPropDto("ok"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход", description = "Выход с учётной записи")
    public BaseResponseDto<AdditionalPropDto> logoutPost() {

        return new BaseResponseDto<>(new AdditionalPropDto("ok"));
    }
}