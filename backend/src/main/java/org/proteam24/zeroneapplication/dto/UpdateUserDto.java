package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.proteam24.zeroneapplication.entity.UserEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserDto {
    @Size(min = 2, max = 20, message = "Name should be between 2 and 20 characters")
    @NotBlank(message = "Name is mandatory")
    @Pattern(regexp = "[a-zA-Zа-яА-Я]+", message = "Name should have only literal")
    @JsonProperty("first_name")
    private String firstName;

    @Size(min = 2, max = 20, message = "LastName should be between 2 and 20 characters")
    @NotBlank(message = "LastName is mandatory")
    @Pattern(regexp = "[a-zA-Zа-яА-Я]+", message = "LastName should have only literal")
    @JsonProperty("last_name")
    private String lastName;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("birth_date")
    private LocalDateTime birthDate;

    @Size(min = 10, max = 11, message = "Phone number should have 11 numeral")
    @Pattern(regexp = "[0-9]+")
    private String phone;
    private String photo;
    private String about;
    @Size(min = 2, message = "city should be more 2 characters")
    @Pattern(regexp = "[a-zA-Zа-яА-Я- 0-9/()№]+", message = "city should have only literal")
    private String city;
    @Size(min = 2, message = "LastName should be more 2 characters")
    @Pattern(regexp = "[a-zA-Zа-яА-Я]+", message = "country should have only literal")
    private String country;



    public UserEntity toUser(UserEntity userEntity) {
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setBirthDate(birthDate);
        userEntity.setPhone(phone);
        userEntity.setPhoto(photo);
        userEntity.setAbout(about);
        userEntity.setCity(city);
        userEntity.setCountry(country);
        return userEntity;
    }
}
