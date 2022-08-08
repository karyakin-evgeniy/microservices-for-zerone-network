package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.proteam24.zeroneapplication.entity.LikeEntity;
import org.proteam24.zeroneapplication.entity.PostEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LikeDto {

    private List<Long> users;

    private String likes;

    public static LikeDto fromLike(List<LikeEntity> likes, String type) {
        LikeDto likeDto = new LikeDto();
        List<Long> users = new ArrayList<>();
        likes.forEach(likeEntity -> users.add(likeEntity.getPerson().getId()));
        likeDto.setUsers(users);
        likeDto.setLikes(type);
        return likeDto;
    }
}
