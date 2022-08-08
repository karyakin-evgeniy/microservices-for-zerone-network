package org.proteam24.zeroneapplication.service;

import org.proteam24.zeroneapplication.dto.*;
import org.proteam24.zeroneapplication.entity.PostEntity;

import java.security.Principal;
import java.util.List;

public interface PostService {
    BaseResponseSomeDto<List<PostDto>> getSomePosts(int offset, int perPage, String email);

    PostEntity findById(Long id);

    void delete(Long id);

    BaseResponseSomeDto<List<PostDto>> getUserPosts(Long userId, int offset, int itemPerPage, Principal principal);

    BaseResponseDto<PostDto> createPost(CreatePostDto createPostDto, Long authorId, String publishDate);

    BaseResponseDto<TagDto> createTag(TagDto tagDto);

    BaseResponseSomeDto<List<TagDto>> getTags(String tag, int offset, int itemPerPage);

    BaseResponseDto<AdditionalPropDto> deleteTag(Long id);

    BaseResponseDto<PostDto> updatePost(Long id, CreatePostDto createPostDto, String email);

    BaseResponseDto<PostDto> getPostById(Long postId, String email);

    BaseResponseDto<PostDto> deletePost(Long postId, Principal principal);

    BaseResponseDto<PostDto> recoverPost(Long postId, Principal principal);
}
