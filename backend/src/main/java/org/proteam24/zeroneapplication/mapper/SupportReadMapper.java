package org.proteam24.zeroneapplication.mapper;

import org.proteam24.zeroneapplication.entity.Support;
import org.proteam24.zeroneapplication.dto.SupportReadDto;
import org.springframework.stereotype.Component;


@Component
public class SupportReadMapper implements Mapper<Support, SupportReadDto> {

    public SupportReadDto map(Support object) {
        return new SupportReadDto("OK");
    }
}
