package org.proteam24.zeroneapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.proteam24.zeroneapplication.dto.BaseResponseSomeDto;
import org.proteam24.zeroneapplication.dto.CreatePostDto;
import org.proteam24.zeroneapplication.dto.PostDto;
import org.proteam24.zeroneapplication.service.PostService;
import org.proteam24.zeroneapplication.service.SearchService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/post")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Посты", description = "Работа с постами")
public class PostController {
    private final PostService postService;
    private final SearchService searchService;

    public PostController(PostService postService, SearchService searchService) {
        this.postService = postService;
        this.searchService = searchService;
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Удалить пост", description = "Удаление поста")
    public BaseResponseDto<PostDto> deletePost(@PathVariable Long id, Principal principal){

        return postService.deletePost(id, principal);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Изменить пост", description = "Изменение поста")
    public BaseResponseDto<PostDto> updatePost(@PathVariable(name = "id") Long id,
                                      @RequestBody CreatePostDto createPostDto, Principal principal){

        if(principal != null){
            return postService.updatePost(id,createPostDto,principal.getName());
        }
        return null;
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Получение поста", description = "Получение конкретного поста")
    public BaseResponseDto<PostDto> getPost(@PathVariable Long id, Principal principal){

        return postService.getPostById(id, principal.getName());
    }

    @GetMapping
    @Operation(summary = "Получить посты в поиске", description = "Получение постов согласно критериям поиска")
    public BaseResponseSomeDto<List<PostDto>> getSearchedPosts(
            @RequestParam(value = "date_from", required = false, defaultValue = "") String dateFrom,
            @RequestParam(value = "date_to", required = false, defaultValue = "") String dateTo,
            @RequestParam(value = "text", required = false, defaultValue = "") String text,
            @RequestParam(value = "author", required = false, defaultValue = "") String author,
            @RequestParam(value = "tag", required = false, defaultValue = "") String tag,
            @RequestParam(value = "itemPerPage", required = false, defaultValue = "10") Integer itemPerPage,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset
            ) {
        return searchService.getSearchedPosts(dateFrom, dateTo, text, author, tag, itemPerPage, offset);
    }

    @PutMapping(value = "/{id}/recover")
    @Operation(summary = "Восстановление поста", description = "Восстановление удалённого поста")
    public BaseResponseDto<PostDto> recoverPost(@PathVariable Long id, Principal principal) {
        return postService.recoverPost(id, principal);
    }
}
