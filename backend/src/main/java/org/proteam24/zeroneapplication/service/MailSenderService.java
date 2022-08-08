package org.proteam24.zeroneapplication.service;

public interface MailSenderService {
    void send(String emailTo, String subject, String message);
}
