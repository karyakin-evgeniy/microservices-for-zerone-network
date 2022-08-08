package org.proteam24.zeroneapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.proteam24.zeroneapplication.dto.*;
import org.proteam24.zeroneapplication.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearer-key")
@RequestMapping(value = "/api/v1/users/{id}/wall")
@Tag(name = "Стена пользователя", description = "Работа с постами пользователя")
public class WallController {
    private final PostService postService;

    public WallController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @Operation(summary = "Создание поста", description = "Создание поста на стене пользователя")
    public BaseResponseDto<PostDto> createPost(@PathVariable(name = "id") Long id, @RequestBody CreatePostDto createPostDto, @RequestParam(name = "publish_date", defaultValue = "0") String publishDate, Principal principal) {
        if (principal != null) {
            return postService.createPost(createPostDto, id, publishDate);
        }
        return null;
    }

    @GetMapping
    @Operation(summary = "Получение постов пользователя", description = "Получение постов по id пользователя")
    public BaseResponseSomeDto<List<PostDto>> getWallPosts(@PathVariable(name = "id") Long id, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int itemPerPage, Principal principal) {
        return postService.getUserPosts(id, offset, itemPerPage, principal);
    }
}
