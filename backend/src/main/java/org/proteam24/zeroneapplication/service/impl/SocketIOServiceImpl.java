package org.proteam24.zeroneapplication.service.impl;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.dto.socketdto.SocketMessageDto;
import org.proteam24.zeroneapplication.dto.socketdto.SocketNotificationDto;
import org.proteam24.zeroneapplication.dto.socketdto.SocketResponseDto;
import org.proteam24.zeroneapplication.entity.DialogEntity;
import org.proteam24.zeroneapplication.entity.MessageEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.hash.ClientHash;
import org.proteam24.zeroneapplication.repository.*;
import org.proteam24.zeroneapplication.security.jwt.JwtTokenProvider;
import org.proteam24.zeroneapplication.service.SocketIOHandlerService;
import org.proteam24.zeroneapplication.service.SocketIOService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;


@Slf4j
@Service(value = "socketIOService")
public class SocketIOServiceImpl implements SocketIOService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final DialogEntityRepository dialogEntityRepository;
    private final MessageEntityRepository messageEntityRepository;
    private SocketIOServer socketIOServer;
    private final ClientHashRepository clientHashRepository;
    private final OnLineDialogRepository onLineDialogRepository;

    public SocketIOServiceImpl(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, DialogEntityRepository dialogEntityRepository, MessageEntityRepository messageEntityRepository, SocketIOServer socketIOServer, ClientHashRepository clientHashRepository, OnLineDialogRepository onLineDialogRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.dialogEntityRepository = dialogEntityRepository;
        this.messageEntityRepository = messageEntityRepository;
        this.socketIOServer = socketIOServer;
        this.onLineDialogRepository = onLineDialogRepository;
        this.clientHashRepository = clientHashRepository;
    }

    @PostConstruct
    private void autoStartup() {
        start();
    }
    @PreDestroy
    private void autoStop() {
        stop();
    }
    @Override
    public void start() {
        socketIOServer.addListeners(new SocketIOHandlerService(jwtTokenProvider,
                userRepository,
                dialogEntityRepository,
                messageEntityRepository,
                socketIOServer,
                clientHashRepository,
                onLineDialogRepository));
        socketIOServer.start();
    }
    @Override
    public void stop() {
        if (socketIOServer != null) {
            socketIOServer.stop();
            socketIOServer = null;
        }
    }
    @Override
    public void pushMessageToUser(Long userId, DialogEntity dialog, MessageEntity messageEntity, Long recipientUnReadCount) {
        Long recipientUserId = messageEntity.getRecipient().getId();
        if (clientHashRepository.findById(recipientUserId).isPresent()) {
            ClientHash clientHash = clientHashRepository.findById(recipientUserId).get();
            SocketIOClient recipientClient = socketIOServer.getClient(UUID.fromString(clientHash.getSessionId()));
            SocketMessageDto socketMessageDto = new SocketMessageDto(messageEntity);
            socketMessageDto.setSendByMe(true);
            SocketResponseDto<SocketMessageDto> socketResponseDto = new SocketResponseDto<>(socketMessageDto, "");
            recipientClient.sendEvent(MESSAGE, socketResponseDto);
            recipientClient.sendEvent(UNREAD_RESPONSE, recipientUnReadCount);
        }
    }
    @Override
    public void pushNotificationToUser(SocketNotificationDto socketNotificationDto, UserEntity author) {
        Long recipientUserId = author.getId();
        if (clientHashRepository.findById(recipientUserId).isPresent()){
            ClientHash clientHash = clientHashRepository.findById(recipientUserId).get();
            SocketIOClient recipientClient = socketIOServer.getClient(UUID.fromString(clientHash.getSessionId()));
            recipientClient.sendEvent(COMMENT_NOTIFICATION_RESPONSE, socketNotificationDto);
        }
    }
}
