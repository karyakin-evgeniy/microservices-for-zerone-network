package org.proteam24.zeroneapplication.mapper;

import org.proteam24.zeroneapplication.dto.UserCreateEditDto;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserCreateEditMapper implements Mapper<UserCreateEditDto, UserEntity> {

    @Override
    public UserEntity map(UserCreateEditDto fromObject, UserEntity toObject) {
        copy(fromObject, toObject);

        return toObject;
    }

    @Override
    public UserEntity map(UserCreateEditDto object) {
        UserEntity user = new UserEntity();
        copy(object, user);

        return user;
    }

    private void copy(UserCreateEditDto object, UserEntity user) {
        user.setFirstName(object.getFirstname());
        user.setLastName(object.getLastname());
        user.setBirthDate(object.getBirthDate());
    }
}
