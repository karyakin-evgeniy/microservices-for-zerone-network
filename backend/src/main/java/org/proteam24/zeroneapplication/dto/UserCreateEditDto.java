package org.proteam24.zeroneapplication.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class UserCreateEditDto {

    String firstname;
    String lastname;
    LocalDateTime birthDate;
}
