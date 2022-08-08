package org.proteam24.zeroneapplication.service;

import org.proteam24.zeroneapplication.dto.*;

import java.util.List;
import java.util.Map;

public interface DialogService {

    BaseResponseDto<LastDialogDto> getDialogs(String email, DialogsUserDto dialogsUserDto);

    BaseResponseSomeDto<List<LastDialogDto>> getAllDialogs(String name, int offset, int itemPerPage);

    BaseResponseDto<CountDto> getUnReaded(String name);


    BaseResponseSomeDto<List<MessageDto>> getMessages(Long id, int itemPerPage, int offset, String name);

    BaseResponseDto<MessageDto> postMessage(Long id, String name, MessageDto messageDto);
}
