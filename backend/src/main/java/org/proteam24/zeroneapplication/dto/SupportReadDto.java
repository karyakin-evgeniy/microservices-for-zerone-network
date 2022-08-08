package org.proteam24.zeroneapplication.dto;

import lombok.Value;

@Value
public class SupportReadDto {

    Long id;
    String firstname;
    String lastname;
    String email;
    String message;
}
