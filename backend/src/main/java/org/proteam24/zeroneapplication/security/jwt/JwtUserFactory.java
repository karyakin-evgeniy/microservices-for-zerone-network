package org.proteam24.zeroneapplication.security.jwt;


import org.proteam24.zeroneapplication.entity.enumerated.Status;
import org.proteam24.zeroneapplication.entity.UserEntity;


public final class JwtUserFactory {
    public JwtUserFactory() {
    }
    public static JwtUser create(UserEntity userEntity){
        return new JwtUser(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getStatus().equals(Status.ACTIVE),
                userEntity.getUpdated()
        );
    }
}
