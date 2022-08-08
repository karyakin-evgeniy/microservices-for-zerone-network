package org.proteam24.zeroneapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.proteam24.zeroneapplication.dto.BaseResponseSomeDto;
import org.proteam24.zeroneapplication.dto.PostDto;
import org.proteam24.zeroneapplication.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/feeds")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Новости", description = "Получение списка последних созданных постов")
public class FeedsController {
    private final PostService postService;

    public FeedsController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    @Operation(summary = "Получение новостей", description = "Получение списка постов от всех пользователей")
    public BaseResponseSomeDto<List<PostDto>> getPostsInFeeds(@RequestParam(defaultValue = "") String text, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int itemPerPage, Principal principal) {

        return postService.getSomePosts(offset, itemPerPage, principal.getName());
    }
}
