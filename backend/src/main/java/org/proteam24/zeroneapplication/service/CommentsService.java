package org.proteam24.zeroneapplication.service;

import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.proteam24.zeroneapplication.dto.BaseResponseSomeDto;
import org.proteam24.zeroneapplication.dto.CommentDto;
import org.proteam24.zeroneapplication.dto.CreateCommentDto;
import org.proteam24.zeroneapplication.entity.CommentEntity;
import org.proteam24.zeroneapplication.entity.LikeEntity;
import org.proteam24.zeroneapplication.entity.PostEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.entity.enumerated.NotificationType;
import org.proteam24.zeroneapplication.repository.*;
import org.proteam24.zeroneapplication.security.jwt.JwtTokenProvider;
import org.proteam24.zeroneapplication.service.impl.NotificationServiceImpl;
import org.proteam24.zeroneapplication.service.impl.SocketIOServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikesRepository likesRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final NotificationServiceImpl notificationService;

    public CommentsService(CommentsRepository commentsRepository, PostRepository postRepository, UserRepository userRepository, LikesRepository likesRepository, NotificationSettingsRepository notificationSettingsRepository, NotificationServiceImpl notificationService) {
        this.commentsRepository = commentsRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likesRepository = likesRepository;
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.notificationService = notificationService;
    }

    public BaseResponseDto<CommentDto> createComment(Long postId, CreateCommentDto createCommentDto, String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        PostEntity postEntity = postRepository.getById(postId);
        CommentEntity entity = createCommentDto.toComment();
        entity.setPostEntity(postEntity);
        entity.setAuthor(userEntity);
        CommentEntity createdCommentEntity = commentsRepository.save(entity);
        CommentDto commentDto = CommentDto.fromComment(createdCommentEntity);
        commentDto.setImages(createCommentDto.getImages());
        commentDto.setMyLike(false);


        boolean myComment = true;
        if (createdCommentEntity.getParentId() != null) {
            CommentEntity parentComment = commentsRepository.getById(createdCommentEntity.getParentId());
            int countByUserAndType = notificationSettingsRepository.countByUserAndType(createdCommentEntity.getAuthor(), NotificationType.COMMENT_COMMENT.toString());
            myComment = !parentComment.getAuthor().getId().equals(postEntity.getAuthor().getId());
            if (myComment && countByUserAndType > 0) {
                CommentEntity commentEntity = commentsRepository.getById(createdCommentEntity.getParentId());
                notificationService.pushNotificationCommentToComment(commentEntity, createdCommentEntity);
            }
            myComment = !parentComment.getAuthor().getId().equals(createdCommentEntity.getAuthor().getId());
            if (myComment && countByUserAndType > 0) {
                if (!postEntity.getAuthor().getId().equals(createdCommentEntity.getAuthor().getId())) {
                    if (!postEntity.getAuthor().getId().equals(parentComment.getAuthor().getId())) {
                        CommentEntity commentEntity = commentsRepository.getById(createdCommentEntity.getParentId());
                        notificationService.pushNotificationCommentToComment(commentEntity, createdCommentEntity);
                    }
                }
            }
        }
        if (!postEntity.getAuthor().getId().equals(createdCommentEntity.getAuthor().getId()) && myComment) {
            int countByUserAndType = notificationSettingsRepository.countByUserAndType(postEntity.getAuthor(), NotificationType.POST_COMMENT.toString());
            if (countByUserAndType > 0) {
                notificationService.pushNotification(postEntity, userEntity, createdCommentEntity.getId());
            }
        }
        return new BaseResponseDto<>(commentDto, "");
    }

    public BaseResponseSomeDto<List<CommentDto>> getPostComments(Long postId, int offset, int perPage, String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        Pageable pageable = PageRequest.of(offset /= perPage, perPage);

        Page<CommentEntity> comments = commentsRepository.findByPostEntity(postRepository.getById(postId), pageable);

        log.info("IN getPostComments - {} comments found", comments.getTotalElements());
        List<CommentDto> commentsDto = new ArrayList<>();
        for (CommentEntity commentEntity : comments) {
            if (commentEntity.getParentId() == null) {
                CommentDto commentDto = addMyLike(commentEntity, userEntity.getId());
                editDeletedComment(commentDto);
                List<CommentEntity> subComments = commentsRepository.findByParentId(commentEntity.getId());
                if (subComments != null) {
                    Collections.reverse(subComments);
                    commentDto.getSubComments().addAll(ListCommentEntityToListCommentDto(subComments));
                }
                commentsDto.add(commentDto);
            }
        }
        return new BaseResponseSomeDto<>(commentsDto, offset, perPage, (int) comments.getTotalElements(), "");
    }

    public static List<CommentDto> ListCommentEntityToListCommentDto(List<CommentEntity> commentEntities) {
        List<CommentDto> subCommentsDto = new ArrayList<>();
        for (CommentEntity commentEntity : commentEntities) {
            CommentDto commentDto = CommentDto.fromComment(commentEntity);
            editDeletedComment(commentDto);
            subCommentsDto.add(commentDto);

        }
        return subCommentsDto;
    }

    public static CommentDto editDeletedComment(CommentDto commentDto) {
        if (commentDto.getIsDeleted()) {
            commentDto.setCommentText("Комментарий был удалён!");
        }
        return commentDto;
    }

    public BaseResponseDto<CommentDto> deleteComment(Long postId, Long commentId) {
        CommentEntity commentEntity = commentsRepository.getById(commentId);
        commentEntity.setDeleted(true);
        commentsRepository.save(commentEntity);
        CommentDto commentDto = CommentDto.fromComment(commentEntity);

        return new BaseResponseDto<>(commentDto, "");
    }

    public BaseResponseDto<CommentDto> editComment(Long postId, Long commentId, CreateCommentDto createCommentDto) {
        CommentEntity commentEntity = commentsRepository.getById(commentId);
        CommentEntity commentInRequest = createCommentDto.toComment();
        log.info("in request we have " + commentInRequest.getCommentText());
        commentEntity.setCommentText(createCommentDto.getCommentText());
        commentsRepository.save(commentEntity);
        CommentDto commentDto = CommentDto.fromComment(commentEntity);
        return new BaseResponseDto<>(commentDto, "");
    }

    public BaseResponseDto<CommentDto> recoverComment(Long postId, Long commentId) {
        CommentEntity commentEntity = commentsRepository.getById(commentId);
        commentEntity.setDeleted(false);
        commentsRepository.save(commentEntity);
        CommentDto commentDto = CommentDto.fromComment(commentEntity);
        return new BaseResponseDto<>(commentDto, "");
    }

    private CommentDto addMyLike(CommentEntity commentEntity, Long userId) {
        CommentDto commentDto = CommentDto.fromComment(commentEntity);
        LikeEntity likeEntity = likesRepository.getCommentLikeByPerson(userRepository.getById(userId), commentEntity.getId());
        log.info("In addMyLike by comment {} user {} like {}", commentDto.getId(), userId, likeEntity);
        if (likeEntity != null) {
            commentDto.setMyLike(true);
        }

        return commentDto;
    }
}
