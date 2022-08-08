package org.proteam24.zeroneapplication.service;

import org.proteam24.zeroneapplication.dto.BaseResponseSomeDto;
import org.proteam24.zeroneapplication.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    BaseResponseSomeDto<List<NotificationDto>> getNotifications(int offset, int perPage, String email);
    BaseResponseSomeDto<List<NotificationDto>> putNotifications(int offset, int perPage, String email, Long notificationId);
}
