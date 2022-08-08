package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.proteam24.zeroneapplication.entity.PostEntity;
import org.proteam24.zeroneapplication.entity.Tag2PostEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {
    private Long id;
    private LocalDateTime time;
    private UserDto author;
    private String title;
    @JsonProperty("post_text")
    private String postText;
    private BaseResponseSomeDto<List<CommentDto>> comments;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
    @JsonProperty("is_deleted")
    private boolean isDeleted;
    private int likes;
    private List<String> tags;
    @JsonProperty("my_like")
    private boolean myLike;
    private String type;

    public static PostDto fromPost(PostEntity postEntity, BaseResponseSomeDto<List<CommentDto>> comments) {
        PostDto postDto = new PostDto();
        postDto.setId(postEntity.getId());
        postDto.setPostText(postEntity.getPostText());
        postDto.setAuthor(UserDto.fromUser(postEntity.getAuthor()));
        postDto.setComments(comments);
        postDto.setTitle(postEntity.getTitle());
        postDto.setBlocked(postEntity.getIsBlocked());
        postDto.setDeleted(postEntity.getIsDeleted());
        postDto.setTime(postEntity.getTime());
        postDto.setLikes(postEntity.getLikes());
        List<String> tagList = new ArrayList<>();
        List<Tag2PostEntity> tag2PostEntities = postEntity.getTag2Posts();
        if (tag2PostEntities != null) {
            postEntity.getTag2Posts().forEach(tag2PostEntity -> tagList.add(tag2PostEntity.getTag().getName()));
        }
        postDto.setTags(tagList);
        postDto.setType("POSTED");

        return postDto;
    }
}
