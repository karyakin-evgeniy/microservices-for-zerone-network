package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponseDto<T> {
    private T data;
    private String error;
    private LocalDateTime timestamp;

    public BaseResponseDto(T data, String error) {
        this.data = data;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }
    public BaseResponseDto(T data) {
        this.data = data;
        this.error = "";
        this.timestamp = LocalDateTime.now();
    }
}
