package org.proteam24.zeroneapplication.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.dto.*;
import org.proteam24.zeroneapplication.service.impl.DialogServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1")
@Slf4j
@Tag(name = "Диалоги", description = "Работа с диалогами")
public class DialogController {

    private final DialogServiceImpl dialogService;

    public DialogController(DialogServiceImpl dialogService) {
        this.dialogService = dialogService;
    }

    @GetMapping(value = "/dialogs")
    @Operation(summary = "Получение списка диалогов", description = "Получение списка диалогов пользовтеля")
    public BaseResponseSomeDto<List<LastDialogDto>> getAllDialogs(Principal principal, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int itemPerPage){
        if(principal != null){
            return dialogService.getAllDialogs(principal.getName(), offset, itemPerPage);
        }
        return null;
    }

    @PostMapping(value = "/dialogs")
    @Operation(summary = "Создание диалога", description = "Создание диалога с пользователем")
    public BaseResponseDto<LastDialogDto> postDialogs(@RequestBody DialogsUserDto dialogsUserDto, Principal principal){

        if(principal != null && dialogsUserDto != null){
            return dialogService.getDialogs(principal.getName(),dialogsUserDto);
        }
        return null;
    }

    @GetMapping(value = "/dialogs/unreaded")
    @Operation(summary = "Напрочитанные диалоги", description = "Получение непрочитанных диалогов")
    public BaseResponseDto<CountDto> unreaded(Principal principal){
        if(principal != null){
            return dialogService.getUnReaded(principal.getName());
        }
        return null;
    }

    @GetMapping(value = "/dialogs/{id}/messages")
    @Operation(summary = "Получение сообщений в диалоге", description = "Получение списка сообщений в диалоге")
    public BaseResponseSomeDto<List<MessageDto>> getMessages(@PathVariable(name = "id") Long id, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int itemPerPage, Principal principal){
        if(principal != null && id != 0){
            return dialogService.getMessages(id, itemPerPage, offset, principal.getName());
        }
        return null;
    }

    @PostMapping(value = "/dialogs/{id}/messages")
    @Operation(summary = "Написание сообщения", description = "Написание сообщения в диалоге")
    public BaseResponseDto<MessageDto> postMessage(@RequestBody MessageDto messageDto, @PathVariable(name = "id") Long id, Principal principal){
        if(principal != null && id != 0){
            return dialogService.postMessage(id, principal.getName(), messageDto);
        }
        return null;
    }
}
