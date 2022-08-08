package org.proteam24.zeroneapplication.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class AdditionalPropSettingsDto {
    private Boolean postComment;
    private Boolean commentComment;
    private Boolean friendsRequest;
}
