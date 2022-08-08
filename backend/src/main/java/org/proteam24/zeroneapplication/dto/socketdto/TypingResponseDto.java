package org.proteam24.zeroneapplication.dto.socketdto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypingResponseDto {
    private Long authorId;
    private String author;
    private Long dialog;
}
