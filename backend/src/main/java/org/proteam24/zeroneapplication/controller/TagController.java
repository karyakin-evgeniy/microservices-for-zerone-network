package org.proteam24.zeroneapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.proteam24.zeroneapplication.dto.*;
import org.proteam24.zeroneapplication.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/tags")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Теги", description = "Работа с тегами")
public class TagController {
    private final PostService postService;

    public TagController(PostService postService) {

        this.postService = postService;
    }

    @PostMapping
    @Operation(summary = "Добавление тега", description = "Создание нового тега")
    public BaseResponseDto<TagDto> createTag(@RequestBody TagDto tagDto) {

        return postService.createTag(tagDto);
    }

    @GetMapping
    @ResponseBody
    @Operation(summary = "Получение тегов", description = "Получение списка тегов")
    public BaseResponseSomeDto<List<TagDto>> getTags(@RequestParam(name = "tag", defaultValue = "") String tag, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int itemPerPage) {
        return postService.getTags(tag, offset, itemPerPage);
    }

    @DeleteMapping
    @Operation(summary = "Удаление тега", description = "Удаление тега")
    public BaseResponseDto<AdditionalPropDto> deleteTag(@RequestParam(name = "id") Long id) {
        return postService.deleteTag(id);
    }
}


