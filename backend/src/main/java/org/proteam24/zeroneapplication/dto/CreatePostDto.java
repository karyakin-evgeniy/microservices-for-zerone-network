package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.proteam24.zeroneapplication.entity.PostEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class CreatePostDto {
    private Long id;

    @Size(min = 2, max = 50, message = "Title should be between 2 and 50 characters")
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Size(min = 2, max = 255, message = "Post text should be between 2 and 255 characters")
    @NotBlank(message = "Post text is mandatory")
    @JsonProperty("post_text")
    private String postText;
    private List<String> tags;

    public PostEntity toUser() {
        PostEntity postEntity = new PostEntity();
        postEntity.setPostText(postText);
        postEntity.setTitle(title);
        postEntity.setIsDeleted(false);
        postEntity.setIsBlocked(false);
        postEntity.setLikes(0);

        return postEntity;
    }
}
