package org.proteam24.zeroneapplication.config.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class MailParam {
    private String host;
    private String username;
    private String password;
    private int port;
    private String protocol;
    private String debug;
}