package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.proteam24.zeroneapplication.entity.CommentEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {
    private Long id;
    private LocalDateTime time;
    @JsonProperty("post_id")
    private Long postId;
    private UserDto author;
    @JsonProperty("comment_text")
    private String commentText;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
    @JsonProperty("is_deleted")
    private boolean isDeleted;
    private List<ImageDto> images = new ArrayList<>();
    @JsonProperty("parent_id")
    private Long parentId;
    @JsonProperty("sub_comments")
    private List<CommentDto> subComments = new ArrayList<>();
    @JsonProperty("my_like")
    private boolean myLike;
    private int likes;

    public static CommentDto fromComment(CommentEntity commentEntity) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentEntity.getId());
        commentDto.setTime(commentEntity.getTime());
        commentDto.setPostId(commentEntity.getPostEntity().getId());
        commentDto.setAuthor(UserDto.fromUser(commentEntity.getAuthor()));
        commentDto.setCommentText(commentEntity.getCommentText());
        commentDto.setBlocked(commentEntity.getIsBlocked());
        commentDto.setDeleted(commentEntity.isDeleted());
        commentDto.setLikes(commentEntity.getLikes());
        commentDto.setMyLike(false);
        commentDto.setParentId(commentEntity.getParentId());
        return commentDto;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }
}
