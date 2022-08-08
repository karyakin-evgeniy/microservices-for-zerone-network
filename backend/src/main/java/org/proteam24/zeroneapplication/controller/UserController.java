package org.proteam24.zeroneapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.proteam24.zeroneapplication.dto.*;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.repository.jooq.UserJooqRepository;
import org.proteam24.zeroneapplication.security.jwt.JwtTokenProvider;
import org.proteam24.zeroneapplication.service.SearchService;
import org.proteam24.zeroneapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Профиль", description = "Работа с профилем")
public class UserController {
    private final UserService userService;
    private final UserJooqRepository userJooqRepository;
    private final SearchService searchService;


    public UserController(UserService userService, UserJooqRepository userJooqRepository, SearchService searchService) {
        this.userService = userService;
        this.userJooqRepository = userJooqRepository;
        this.searchService = searchService;
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Получение пользователя", description = "Получение пользователя по id")
    public BaseResponseDto<UserDto> getUserById(@PathVariable(name = "id") Long id) {
        return userService.getUserById(id);

    }

    @GetMapping(value = "/me")
    @Operation(summary = "Получение текущего пользователя", description = "Получение авторизованного пользователя")
    public BaseResponseDto<UserDto> getUser(Principal principal) {
        return userService.getUser(principal.getName());
    }

    @PutMapping(value = "/me")
    @Operation(summary = "Обновление профиля пользователя", description = "Обновление данных пользователя")
    public BaseResponseDto<UserDto> updateProfile(@Valid @RequestBody UpdateUserDto userDto, Principal principal) {
        if (principal != null) {
            return userService.updateProfile(userDto, principal.getName());
        }
        return null;
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск пользователей", description = "Получение списка пользователей согласно поисковым запросам")
    public BaseResponseSomeDto<List<UserDto>> getAllUsersByName(
            @RequestParam(value = "first_name", required = false, defaultValue = "") String firstName,
            @RequestParam(value = "last_name", required = false, defaultValue = "") String lastName,
            @RequestParam(value = "country", required = false, defaultValue = "") String country,
            @RequestParam(value = "city", required = false, defaultValue = "") String city,
            @RequestParam(value = "itemPerPage", required = false, defaultValue = "10") Integer itemPerPage,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            Principal principal) {
        return searchService.getSearchedUsers(userService.getUser(principal.getName()).getData().getId(),
                firstName, lastName, country, city, itemPerPage, offset);
    }

}