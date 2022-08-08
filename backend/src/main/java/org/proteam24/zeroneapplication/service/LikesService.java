package org.proteam24.zeroneapplication.service;

import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.proteam24.zeroneapplication.dto.LikeDto;
import org.proteam24.zeroneapplication.dto.RequestLikeDto;
import org.proteam24.zeroneapplication.entity.CommentEntity;
import org.proteam24.zeroneapplication.entity.LikeEntity;
import org.proteam24.zeroneapplication.entity.PostEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.repository.CommentsRepository;
import org.proteam24.zeroneapplication.repository.LikesRepository;
import org.proteam24.zeroneapplication.repository.PostRepository;
import org.proteam24.zeroneapplication.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LikesService {
    private final CommentsRepository commentsRepository;
    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public LikesService(CommentsRepository commentsRepository, LikesRepository likesRepository, UserRepository userRepository, PostRepository postRepository) {
        this.commentsRepository = commentsRepository;
        this.likesRepository = likesRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public BaseResponseDto<LikeDto> getLikes(Long itemId, String type, Principal principal) {
        UserEntity user = userRepository.findByEmail(principal.getName());
        PostEntity post;
        List<LikeEntity> likeEntities;


        switch (type) {
            case ("Post"):
                post = postRepository.getById(itemId);
                likeEntities = likesRepository.getPostsLike(post);
                break;
            case ("Comment"):
                likeEntities = likesRepository.getCommentsLike(itemId);
                break;
            default:
                likeEntities = new ArrayList<>();
                break;
        }

        return new BaseResponseDto<>(LikeDto.fromLike(likeEntities, type), "");
    }

    public BaseResponseDto<LikeDto> deleteLike(RequestLikeDto requestLikeDto, Principal principal) {
        UserEntity user = userRepository.findByEmail(principal.getName());
        LikeEntity likeEntity;
        String type = requestLikeDto.getType();

        switch (type) {
            case ("Post"):
                PostEntity post = postRepository.getById(requestLikeDto.getItemId());
                likeEntity = likesRepository.getPostLikeByPerson(user, post);
                post.setLikes(post.getLikes() - 1);
                postRepository.save(post);
                likesRepository.delete(likeEntity);
                break;
            case ("Comment"):
                CommentEntity comment = commentsRepository.getById(requestLikeDto.getItemId());
                likeEntity = likesRepository.getCommentLikeByPerson(user, comment.getId());
                comment.setLikes(comment.getLikes() - 1);
                commentsRepository.save(comment);
                likesRepository.delete(likeEntity);
                break;
            default:
                break;
        }

        return getLikes(requestLikeDto.getItemId(), type, principal);
    }

    public BaseResponseDto<LikeDto> putLike(RequestLikeDto requestLikeDto, Principal principal) {
        UserEntity user = userRepository.findByEmail(principal.getName());
        LikeEntity likeEntity = new LikeEntity();
        PostEntity post;
        String type = requestLikeDto.getType();
        likeEntity.setType(type);
        likeEntity.setPerson(user);
        likeEntity.setTime(LocalDateTime.now());


        switch (type) {
            case ("Post"):
                post = postRepository.getById(requestLikeDto.getItemId());
                post.setLikes(post.getLikes() + 1);
                postRepository.save(post);
                likeEntity.setPost(post);
                likesRepository.save(likeEntity);
                break;
            case ("Comment"):
                post = postRepository.getById(requestLikeDto.getPostId());
                likeEntity.setPost(post);
                likeEntity.setEntityId(requestLikeDto.getItemId());
                CommentEntity commentEntity = commentsRepository.getById(requestLikeDto.getItemId());
                commentEntity.setLikes(commentEntity.getLikes() + 1);
                commentsRepository.save(commentEntity);
                likesRepository.save(likeEntity);
                break;
            default:
                break;
        }


        return getLikes(requestLikeDto.getItemId(), type, principal);
    }
}
