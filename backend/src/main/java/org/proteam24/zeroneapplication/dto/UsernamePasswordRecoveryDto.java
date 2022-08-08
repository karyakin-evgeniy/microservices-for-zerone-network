package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class UsernamePasswordRecoveryDto {

    private String email;
    private String password;
    private String token;

    public UsernamePasswordRecoveryDto(String email, String password, String token) {
        this.email = email;
        this.password = password;
        this.token = token;
    }
}
