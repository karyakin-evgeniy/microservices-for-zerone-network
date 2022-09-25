package org.proteam24.zeroneapplication.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ErrorDto {

    String error;
    LocalDateTime time;
}
