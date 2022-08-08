package org.proteam24.zeroneapplication.mapper;

import org.proteam24.zeroneapplication.entity.Support;
import org.proteam24.zeroneapplication.dto.SupportResponseDto;
import org.springframework.stereotype.Component;


@Component
public class SupportResponseMapper implements Mapper<Support, SupportResponseDto> {

    public SupportResponseDto map(Support object) {
        return new SupportResponseDto("OK");
    }
}
