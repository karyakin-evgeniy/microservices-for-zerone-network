package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.proteam24.zeroneapplication.entity.LikeEntity;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestLikeDto {

    @JsonProperty("item_id")
    private Long itemId;

    @JsonProperty("post_id")
    private Long postId;

    private String type;

}
