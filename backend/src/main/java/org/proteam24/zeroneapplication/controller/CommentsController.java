package org.proteam24.zeroneapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.proteam24.zeroneapplication.dto.BaseResponseSomeDto;
import org.proteam24.zeroneapplication.dto.CommentDto;
import org.proteam24.zeroneapplication.dto.CreateCommentDto;
import org.proteam24.zeroneapplication.security.jwt.JwtTokenProvider;
import org.proteam24.zeroneapplication.service.CommentsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/post/{id}/comments")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Комментарии", description = "Работа с комментариями")
public class CommentsController {
    private final CommentsService commentsService;
    private final JwtTokenProvider jwtTokenProvider;

    public CommentsController(CommentsService commentsService, JwtTokenProvider jwtTokenProvider) {
        this.commentsService = commentsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping
    @Operation(summary = "Написать комментарий", description = "Написать комментарий для поста")
    public BaseResponseDto<CommentDto> createComment(@PathVariable Long id, @RequestBody CreateCommentDto commentDto, Principal principal) {

        return commentsService.createComment(id, commentDto, principal.getName());
    }

    @GetMapping
    @Operation(summary = "Получить комментарии", description = "Получение комментарий поста")
    public BaseResponseSomeDto<List<CommentDto>> getPostComments(@PathVariable Long id, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "5") int itemPerPage, Principal principal) {
        return commentsService.getPostComments(id, offset, itemPerPage, principal.getName());
    }

    @PutMapping(path = "/{comment_id}")
    @Operation(summary = "Редактировать комментарий", description = "Редактирование своего поста")
    public BaseResponseDto<CommentDto> editComment(@PathVariable Long id, @PathVariable(name = "comment_id") Long commentId,@RequestBody CreateCommentDto createCommentDto, Principal principal) {
        return commentsService.editComment(id, commentId, createCommentDto);
    }

    @DeleteMapping(path = "/{comment_id}")
    @Operation(summary = "Удаление комментария", description = "Удаление своего комментария")
    public BaseResponseDto<CommentDto> deleteComment(@PathVariable Long id, @PathVariable(name = "comment_id") Long commentId , Principal principal) {

        return commentsService.deleteComment(id, commentId);
    }

    @PutMapping(path = "/{comment_id}/recover")
    @Operation(summary = "Восстановление комментария", description = "Восстановление удалённого комментария")
    public BaseResponseDto<CommentDto> recoverComment(@PathVariable Long id, @PathVariable(name = "comment_id") Long commentId , Principal principal) {

        return commentsService.recoverComment(id, commentId);
    }

}
