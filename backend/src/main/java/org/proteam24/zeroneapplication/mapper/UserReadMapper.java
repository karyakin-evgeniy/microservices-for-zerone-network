package org.proteam24.zeroneapplication.mapper;

import org.proteam24.zeroneapplication.dto.UserReadDto;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserReadMapper implements Mapper<UserEntity, UserReadDto> {

    @Override
    public UserReadDto map(UserEntity object) {
        return new UserReadDto(
                object.getId(),
                object.getFirstName(),
                object.getLastName(),
                object.getBirthDate()
        );
    }
}
