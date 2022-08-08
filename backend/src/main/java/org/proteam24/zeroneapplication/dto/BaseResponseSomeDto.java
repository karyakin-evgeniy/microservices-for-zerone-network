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
public class BaseResponseSomeDto<T> {

    private int total;
    private int perPage;
    private int offset;
    private T data;
    private String error;
    private LocalDateTime timestamp;

    public BaseResponseSomeDto(T data, int offset, int perPage, int total, String error) {
        this.total = total;
        this.perPage = perPage;
        this.offset = offset;
        this.data = data;
        this.error = error;
        timestamp = LocalDateTime.now();
    }
}
