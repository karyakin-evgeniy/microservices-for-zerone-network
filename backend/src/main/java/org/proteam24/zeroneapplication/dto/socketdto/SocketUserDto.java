package org.proteam24.zeroneapplication.dto.socketdto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.Getter;
import lombok.Setter;
import org.proteam24.zeroneapplication.entity.UserEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SocketUserDto {
    private Long id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("reg_date")
    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant regDate;
    @JsonProperty("birth_date")
    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant birthDate;
    private String email;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    @JsonProperty("messages_permission")
    private String messagesPermission;
    @JsonProperty("last_online_time")
    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant lastOnlineTime;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
    private String token;
    private String passwd1;
    private boolean deleted;

    public static SocketUserDto fromUser(UserEntity userEntity) {
        SocketUserDto userDto = new SocketUserDto();
        userDto.setId(userEntity.getId());
        userDto.setFirstName(userEntity.getFirstName());
        userDto.setLastName(userEntity.getLastName());
        userDto.setRegDate(userEntity.getRegDate().toInstant(ZoneOffset.UTC));
        userDto.setBirthDate(userEntity.getBirthDate().toInstant(ZoneOffset.UTC));
        userDto.setEmail(userEntity.getEmail());
        userDto.setPhone(userEntity.getPhone());
        userDto.setPhoto(userEntity.getPhoto());
        userDto.setAbout(userEntity.getAbout());
        userDto.setCity(userEntity.getCity());
        userDto.setCountry(userEntity.getCountry());
        userDto.setMessagesPermission("ALL");
        userDto.setLastOnlineTime(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        return userDto;
    }

}