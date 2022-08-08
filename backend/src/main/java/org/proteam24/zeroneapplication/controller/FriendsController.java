package org.proteam24.zeroneapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.proteam24.zeroneapplication.dto.BaseResponseSomeDto;
import org.proteam24.zeroneapplication.dto.UserDto;
import org.proteam24.zeroneapplication.service.FriendshipService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Друзья", description = "Работа с друзьями")
public class FriendsController {
    private final FriendshipService friendshipService;

    @GetMapping(value = "/friends")
    public BaseResponseSomeDto<List<UserDto>> getAllFriends(
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "itemPerPage", required = false, defaultValue = "5") int itemPerPage,
            Principal principal
    ) {
        return friendshipService.getFriends(principal, offset, itemPerPage);
    }

    @GetMapping(value = "/friends/request")
    @Operation(summary = "Список заявок", description = "Получение списка заявок в друзья")
    public BaseResponseSomeDto<List<UserDto>> friendRequestList(
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "itemPerPage", required = false, defaultValue = "5") int itemPerPage,
            Principal principal
    ){
        return friendshipService.getFriendshipRequests(principal, offset, itemPerPage);
    }

    @GetMapping(value = "/friends/recommendations")
    @Operation(summary = "Рекомендации", description = "Получение списка рекомендованных в друзья пользователей")
    public BaseResponseSomeDto<List<UserDto>> recommendationsToFriends(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int itemPerPage,
            Principal principal){
        return friendshipService.getRecommendation(principal, offset, itemPerPage);
    }

    @PostMapping(value = "/friends/{id}")
    @Operation(summary = "Добавление в друзья", description = "Запрос на добавление в друзья")
    public BaseResponseDto<UserDto> addingToFriends(
            @PathVariable(name = "id") Long id, Principal principal){
        return friendshipService.addFriend(principal, id);
    }

    @DeleteMapping(value = "/friends/{id}")
    @Operation(summary = "Удаление друзей", description = "Запрос на удалениление в друзей")
    public BaseResponseDto<UserDto> deleteFriend(@PathVariable(name = "id") Long id, Principal principal) {
        return friendshipService.deleteFriend(principal, id);
    }

    @PutMapping(value = "/users/block/{id}")
    @Operation(summary = "Блокировать пользователя", description = "Запрос на блокирование пользователя")
    public BaseResponseDto<UserDto> blockFriend(@PathVariable(name = "id") Long id, Principal principal) {
        return friendshipService.blockFriend(principal, id);
    }

    @DeleteMapping(value = "/users/block/{id}")
    @Operation(summary = "Разблокировать пользователя", description = "Запрос на разблокирование пользователя")
    public BaseResponseDto<UserDto> unblockFriend(@PathVariable(name = "id") Long id, Principal principal) {
        return friendshipService.unblockFriend(principal, id);
    }
}
