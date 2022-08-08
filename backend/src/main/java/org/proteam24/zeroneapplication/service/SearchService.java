package org.proteam24.zeroneapplication.service;

import org.proteam24.zeroneapplication.dto.*;
import org.proteam24.zeroneapplication.entity.PostEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.repository.jooq.PostJooqRepository;
import org.proteam24.zeroneapplication.repository.jooq.UserJooqRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private final UserJooqRepository userJooqRepository;
    private final PostJooqRepository postJooqRepository;
    private final CommentsService commentsService;

    public SearchService(UserJooqRepository userJooqRepository, PostJooqRepository postJooqRepository, CommentsService commentsService) {
        this.userJooqRepository = userJooqRepository;
        this.postJooqRepository = postJooqRepository;
        this.commentsService = commentsService;
    }

    public BaseResponseSomeDto<List<UserDto>> getSearchedUsers(Long searcherId, String firstName, String lastName, String country, String city, Integer itemPerPage, Integer offset) {
        int total = userJooqRepository.getCountTotal(searcherId, firstName, lastName, country, city).size();
        List<UserEntity> list = userJooqRepository.getUsersByMultiField(searcherId, firstName, lastName, country, city, itemPerPage, offset);

        if (total < 1 || list == null) {
            return new BaseResponseSomeDto<>(null ,0, 0, 0, "");
        }

        List<UserDto> result = list.stream().map(UserDto::fromUser).collect(Collectors.toList());
        return new BaseResponseSomeDto<>(result, offset, itemPerPage, total, "");
    }

    public BaseResponseSomeDto<List<PostDto>> getSearchedPosts(String dateFrom, String dateTo, String text, String author, String tag, Integer itemPerPage, Integer offset) {
        var currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        int total = postJooqRepository.getCountTotal(dateFrom, dateTo, text, author, tag).size();
        List<PostEntity> list = postJooqRepository.getPostsByText(dateFrom, dateTo, text, author, tag, itemPerPage, offset);
        if (total < 1 || list == null) {
            return new BaseResponseSomeDto<>(null ,0, 0, 0, "");
        }

        List<PostDto> result = list.stream()
                .map((p) -> PostDto.fromPost(p, commentsService.getPostComments(p.getId(), offset, itemPerPage, currentUserName)))
                .collect(Collectors.toList());
        return new BaseResponseSomeDto<>(result, offset, itemPerPage, total, "");
    }
}
