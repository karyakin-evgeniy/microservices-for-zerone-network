package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.proteam24.zeroneapplication.entity.CommentEntity;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateCommentDto {

    private Long id;
    @JsonProperty("parent_id")
    private Long parentId;
    private List<ImageDto> images;
    @JsonProperty("comment_text")
    private String commentText;


    public CommentEntity toComment() {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setCommentText(commentText);
        commentEntity.setParentId(parentId);
        commentEntity.setDeleted(false);
        commentEntity.setIsBlocked(false);
        commentEntity.setTime(LocalDateTime.now());
        commentEntity.setLikes(0);
        return commentEntity;
    }


}
