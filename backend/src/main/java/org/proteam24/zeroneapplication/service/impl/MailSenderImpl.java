package org.proteam24.zeroneapplication.service.impl;

import org.proteam24.zeroneapplication.config.mail.MailParam;
import org.proteam24.zeroneapplication.service.MailSenderService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderImpl implements MailSenderService {

    private final JavaMailSender mailSender;
    private final MailParam mailParam;

    public MailSenderImpl(JavaMailSender mailSender, MailParam mailParam) {
        this.mailSender = mailSender;
        this.mailParam = mailParam;
    }

    @Override
    public void send(String emailTo, String subject, String message){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailParam.getUsername());
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }
}
