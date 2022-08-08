package org.proteam24.zeroneapplication.service;

import org.proteam24.zeroneapplication.dto.socketdto.SocketNotificationDto;
import org.proteam24.zeroneapplication.entity.DialogEntity;
import org.proteam24.zeroneapplication.entity.MessageEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;

public interface SocketIOService {
    String READ_MESSAGES = "read-messages";

    String UNREAD_RESPONSE = "unread-response";

    String START_TYPING_RESPONSE = "start-typing-response";

    String STOP_TYPING_RESPONSE = "stop-typing-response";

    String AUTH_RESPONSE = "auth-response";

    String MESSAGE = "message";

    String START_TYPING = "start-typing";

    String STOP_TYPING = "stop-typing";

    String AUTH = "auth";

    public static final String COMMENT_NOTIFICATION_RESPONSE = "comment-notification-response";

    // запускаем службу
    void start() throws Exception;

    // Не работает
    void stop();

    // Отправляем информацию
    void pushMessageToUser(Long userId, DialogEntity dialog, MessageEntity messageEntity, Long recipientUnReadCount);

    void pushNotificationToUser(SocketNotificationDto socketNotificationDto, UserEntity author);
}
