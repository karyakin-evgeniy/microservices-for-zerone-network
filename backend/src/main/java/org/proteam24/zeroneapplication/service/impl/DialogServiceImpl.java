package org.proteam24.zeroneapplication.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.dto.*;
import org.proteam24.zeroneapplication.entity.DialogEntity;
import org.proteam24.zeroneapplication.entity.MessageEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.repository.DialogEntityRepository;
import org.proteam24.zeroneapplication.repository.MessageEntityRepository;
import org.proteam24.zeroneapplication.service.DialogService;
import org.proteam24.zeroneapplication.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DialogServiceImpl implements DialogService {

    private final UserService userService;
    private final MessageEntityRepository messageEntityRepository;
    private final DialogEntityRepository dialogEntityRepository;
    private final SocketIOServiceImpl socketIOService;

    public DialogServiceImpl(UserService userService, MessageEntityRepository messageEntityRepository, DialogEntityRepository dialogEntityRepository, SocketIOServiceImpl socketIOService) {
        this.userService = userService;
        this.messageEntityRepository = messageEntityRepository;
        this.dialogEntityRepository = dialogEntityRepository;
        this.socketIOService = socketIOService;
    }

    @Override
    public BaseResponseDto<LastDialogDto> getDialogs(String email, DialogsUserDto dialogsUserDto) {
        UserEntity user = userService.findByEmail(email);
        UserEntity userRecipient = userService.findById(dialogsUserDto.getUsersIds().get(0));

        DialogEntity dialog = dialogEntityRepository.findDialog(user, userRecipient);

        if (dialog == null){
            dialog = dialogEntityRepository.findDialog(userRecipient, user);
            if(dialog == null){
                dialog = new DialogEntity(user,userRecipient);
                dialogEntityRepository.save(dialog);
            }
        }
        MessageEntity messageEntity = messageEntityRepository.findLastMessageDialog(dialog);
        if(messageEntity == null){
            MessageEntity message = new MessageEntity();
            message.setDialog(dialog);
            message.setMessageText("");
            message.setReadStatus(1);
            message.setAuthor(user);
            message.setRecipient(userRecipient);
            message.setTime(LocalDateTime.now());
            messageEntityRepository.save(message);

            LastDialogDto lastDialogDto = new LastDialogDto();
            lastDialogDto.setUserDto(UserDto.fromUser(userRecipient));
            lastDialogDto.setId(dialog.getId());

            MessageDto messageDto = new MessageDto(message);
            lastDialogDto.setMessage(messageDto);
            lastDialogDto.setUnreadCount(0L);

            return new BaseResponseDto<>(lastDialogDto, "");
        }else{
            Long unReadCount = messageEntityRepository.countUnRead(user,messageEntity.getDialog());

            LastDialogDto lastDialogDto = new LastDialogDto();
            lastDialogDto.setId(user.getId());
            lastDialogDto.setUnreadCount(unReadCount);
            MessageDto messageDto = new MessageDto(messageEntity);
            if (messageEntity.getAuthor().getId().equals(user.getId())) {
                messageDto.setSendByMe(false);
            }
            lastDialogDto.setMessage(messageDto);
            lastDialogDto.setUserDto(UserDto.fromUser(userRecipient));

            return new BaseResponseDto<>(lastDialogDto, "");
        }
    }

    @Override
    public BaseResponseSomeDto<List<LastDialogDto>> getAllDialogs(String email, int offset, int perPage) {
        UserEntity user = userService.findByEmail(email);
        Pageable pageable = PageRequest.of(offset /= perPage, perPage);
        Page<DialogEntity> dialogList = messageEntityRepository.findAllDialogs(user, pageable);
        if (dialogList.isEmpty()){
            return new BaseResponseSomeDto<>(null, offset, perPage, 0, "");
        }
        List<Long> total = messageEntityRepository.countAllDialogs(user);
        List<LastDialogDto> lastDialogDtoList = new ArrayList<>();
        dialogList.forEach(dialog -> {
            LastDialogDto lastDialogDto = new LastDialogDto();

            MessageEntity messageEntity = messageEntityRepository.findLastMessageDialog(dialog);//может вызвать ошибку если два сообщения одинаковые по времени
            MessageDto messageDto = new MessageDto(messageEntity);
            if (messageEntity.getAuthor().getId().equals(user.getId())) {
                messageDto.setSendByMe(false);
                lastDialogDto.setUserDto(UserDto.fromUser(messageEntity.getRecipient()));
            }else {
                lastDialogDto.setUserDto(UserDto.fromUser(messageEntity.getAuthor()));
            }
            lastDialogDto.setUnreadCount(messageEntityRepository.countUnRead(user, dialog));
            lastDialogDto.setMessage(messageDto);
            lastDialogDto.setId(dialog.getId());
            lastDialogDtoList.add(lastDialogDto);

        });

        return new BaseResponseSomeDto<>(lastDialogDtoList, offset, perPage, total.size(), "");
    }

    @Override
    public BaseResponseDto<CountDto> getUnReaded(String email) {
        UserEntity user = userService.findByEmail(email);
        List<Long> total = messageEntityRepository.countAllUnReaded(user);
        CountDto countDto = new CountDto();
        countDto.setCount(total.size());

        return new BaseResponseDto<>(countDto);
    }

    @Override
    public BaseResponseSomeDto<List<MessageDto>> getMessages(Long id, int perPage, int offset, String email) {
        UserEntity user = userService.findByEmail(email);
        Pageable pageable = PageRequest.of(offset /= perPage, perPage);
        DialogEntity dialog = dialogEntityRepository.findById(id).get();
        Long total = messageEntityRepository.countAllDialogMessages(dialog);
        Page<MessageEntity> dialogList = messageEntityRepository.findAllMessages(dialog, pageable);

        List<MessageDto> messageDtoList = new ArrayList<>();

        dialogList.forEach(messageEntity -> {
            MessageDto messageDto = new MessageDto(messageEntity);
            if(messageEntity.getAuthor().getId().equals(user.getId())){
                messageDto.setSendByMe(true);
            }
            messageDtoList.add(messageDto);
        });
        messageEntityRepository.saveAll(dialogList);

        return new BaseResponseSomeDto<>(messageDtoList,offset,perPage,total.intValue(),"");
    }

    @Override
    public BaseResponseDto<MessageDto> postMessage(Long id, String email, MessageDto messageDto) {
        UserEntity user = userService.findByEmail(email);
        MessageEntity messageEntity = new MessageEntity();
        if(dialogEntityRepository.findById(id).isEmpty()){
            return new BaseResponseDto<>(null,"Not found");
        }
        DialogEntity dialog = dialogEntityRepository.findById(id).get();
        if (dialog.getUserFirst().getId().equals(user.getId())){
            messageEntity.setRecipient(dialog.getUserSecond());
        }else{
            messageEntity.setRecipient(dialog.getUserFirst());
        }
        messageEntity.setDialog(dialog);
        messageEntity.setTime(LocalDateTime.now());
        messageEntity.setMessageText(messageDto.getMessageText());
        messageEntity.setAuthor(user);
        messageEntity.setReadStatus(0);
        messageEntityRepository.save(messageEntity);
        messageDto = new MessageDto(messageEntity);
        messageDto.setSendByMe(true);
        Long recipientUnReadCount = messageEntityRepository.countUnRead(messageEntity.getRecipient(),dialog);
        socketIOService.pushMessageToUser(user.getId(), dialog, messageEntity, recipientUnReadCount);
        return new BaseResponseDto<>(messageDto);
    }
}
