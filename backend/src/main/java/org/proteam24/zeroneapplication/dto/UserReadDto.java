package org.proteam24.zeroneapplication.dto;

import lombok.Value;
import java.time.LocalDateTime;

@Value
public class UserReadDto {

    Long id;
    String firstname;
    String lastname;
    LocalDateTime birthDate;
}
