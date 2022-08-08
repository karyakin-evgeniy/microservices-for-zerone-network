package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.proteam24.zeroneapplication.entity.UserEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class RegistrationUserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime regDate;
    private String passwd1;


    public UserEntity toUser(){
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword(passwd1.trim());
        userEntity.setFirstName(firstName.trim());
        userEntity.setLastName(lastName.trim());
        userEntity.setRegDate(LocalDateTime.now());
        userEntity.setIsBlocked(false);

        return userEntity;
    }
}
