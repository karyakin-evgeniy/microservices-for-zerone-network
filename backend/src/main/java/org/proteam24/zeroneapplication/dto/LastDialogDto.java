package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LastDialogDto {
    @JsonProperty("unread_count")
    private Long unreadCount;
    @JsonProperty("last_message")
    private MessageDto message;
    private Long id;
    @JsonProperty("recipient_id")
    private UserDto userDto;

}
