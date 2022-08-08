package org.proteam24.zeroneapplication.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.proteam24.zeroneapplication.dto.BaseResponseSomeDto;
import org.proteam24.zeroneapplication.dto.NotificationDto;
import org.proteam24.zeroneapplication.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
@Tag(name = "Уведомления", description = "Работа с уведомлениями")
public class NotificationController {


    private final NotificationService notificationService;

    @GetMapping(value = "/notifications")
    @Operation(summary = "Получить уведомления", description = "Получение всех уведомлений")
    public BaseResponseSomeDto<List<NotificationDto>> notifications(@RequestParam(defaultValue = "0") int offset,
                                                                    @RequestParam(defaultValue = "10") int itemPerPage,
                                                                    Principal principal) {
        return notificationService.getNotifications(offset, itemPerPage, principal.getName());
    }


    @Operation(security = @SecurityRequirement(name = "bearer-key"),
            summary = "Уведомления", description = "Получение уведомлений")
    @PutMapping(value = "/notifications")
    public BaseResponseSomeDto<List<NotificationDto>> readNotification(@RequestParam(defaultValue = "0") int offset,
                                                                       @RequestParam(defaultValue = "10") int itemPerPage,
                                                                       @RequestParam(name = "id") Long notificationId,
                                                                       Principal principal){
        return notificationService.putNotifications(offset, itemPerPage, principal.getName(), notificationId);
    }
}
