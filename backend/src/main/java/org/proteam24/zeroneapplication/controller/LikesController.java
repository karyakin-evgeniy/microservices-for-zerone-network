package org.proteam24.zeroneapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.proteam24.zeroneapplication.dto.LikeDto;
import org.proteam24.zeroneapplication.dto.RequestLikeDto;
import org.proteam24.zeroneapplication.service.LikesService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "api/v1/likes")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Лайки", description = "Работа с лайками")
public class LikesController {
    private final LikesService likesService;

    public LikesController(LikesService likesService) {
        this.likesService = likesService;
    }

    @GetMapping
    @Operation(summary = "Получение лайков", description = "Получение списка лайков")
    public BaseResponseDto<LikeDto> getLikes(@RequestParam(name = "item_id") Long itemId, @RequestParam String type, Principal principal) {
        return likesService.getLikes(itemId, type, principal);
    }

    @DeleteMapping
    @Operation(summary = "Убрать лайк", description = "Удаление лайка от пользователя")
    public BaseResponseDto<LikeDto> deleteLike(@RequestParam(name = "item_id") Long itemId, @RequestParam String type, @RequestBody RequestLikeDto requestLikeDto, Principal principal) {
        return likesService.deleteLike(requestLikeDto, principal);
    }

    @PutMapping
    @Operation(summary = "Поставить лайк", description = "Создать лайк от пользователя")
    public BaseResponseDto<LikeDto> putLike(@RequestBody RequestLikeDto requestLikeDto, Principal principal) {
        return likesService.putLike(requestLikeDto, principal);
    }
}
