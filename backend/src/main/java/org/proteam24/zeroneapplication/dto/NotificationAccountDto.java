package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class NotificationAccountDto {

    @JsonProperty("notification_type")
    private String type;

    private boolean enable;
}
