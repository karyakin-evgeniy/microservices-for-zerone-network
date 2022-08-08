package org.proteam24.zeroneapplication.config.mail;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    private final MailParam mailParam;

    public MailConfig(MailParam mailParam) {
        this.mailParam = mailParam;
    }

    @Bean
    public JavaMailSender getMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailParam.getHost());
        mailSender.setPort(mailParam.getPort());
        mailSender.setUsername(mailParam.getUsername());
        mailSender.setPassword(mailParam.getPassword());
        Properties properties = mailSender.getJavaMailProperties();
        properties.setProperty("mail.transport.protocol",mailParam.getProtocol());
        return mailSender;

    }

}
