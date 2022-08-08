package org.proteam24.zeroneapplication.service;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.dto.socketdto.*;
import org.proteam24.zeroneapplication.entity.DialogEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.hash.ClientHash;
import org.proteam24.zeroneapplication.hash.OnLineDialog;
import org.proteam24.zeroneapplication.repository.*;
import org.proteam24.zeroneapplication.security.jwt.JwtTokenProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.proteam24.zeroneapplication.service.SocketIOService.*;

@Slf4j
public class SocketIOHandlerService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final DialogEntityRepository dialogEntityRepository;
    private final MessageEntityRepository messageEntityRepository;
    private final SocketIOServer socketIOServer;
    private final ClientHashRepository clientHashRepository;
    private final OnLineDialogRepository onLineDialogRepository;

    public SocketIOHandlerService(JwtTokenProvider jwtTokenProvider,
                                  UserRepository userRepository,
                                  DialogEntityRepository dialogEntityRepository,
                                  MessageEntityRepository messageEntityRepository,
                                  SocketIOServer socketIOServer,
                                  ClientHashRepository clientHashRepository,
                                  OnLineDialogRepository onLineDialogRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.dialogEntityRepository = dialogEntityRepository;
        this.messageEntityRepository = messageEntityRepository;
        this.socketIOServer = socketIOServer;
        this.clientHashRepository = clientHashRepository;
        this.onLineDialogRepository = onLineDialogRepository;
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String sessionId = client.getSessionId().toString();
        Iterable<ClientHash> clientHashList = clientHashRepository.findAll();
        if (((Collection<ClientHash>) clientHashList).size() > 0) {
            clientHashList.forEach(clientHash -> {
                if (clientHash.getSessionId().equals(sessionId)) {
                    Long userDeleteID = clientHash.getUserId();
                    Iterable<OnLineDialog> onLineDialogList = onLineDialogRepository.findAll();
                    onLineDialogList.forEach(onLineDialog -> {
                        if (onLineDialog.getUsers().contains(userDeleteID)) {
                            onLineDialogRepository.deleteById(onLineDialog.getId());
                        }
                    });
                    clientHashRepository.deleteById(userDeleteID);
                }
            });

        }
        client.disconnect();
    }

    @OnEvent(value = AUTH)
    private void auth(SocketIOClient client, AckRequest ackSender, AuthDto authDto) {
        String token = authDto.getToken();
        if (token != null) {
            String username = jwtTokenProvider.getUsername(authDto.getToken());
            UserEntity user = userRepository.findByEmail(username);
            ClientHash clientHash = new ClientHash();
            clientHash.setUserId(user.getId());
            clientHash.setSessionId(String.valueOf(client.getSessionId()));
            clientHashRepository.save(clientHash);
            client.sendEvent(AUTH_RESPONSE, "ok");
        } else {
            client.sendEvent(AUTH_RESPONSE, "not");
        }
    }

    @OnEvent(value = READ_MESSAGES)
    private void onEvent(SocketIOClient client, AckRequest ackSender, DialogDto data) {
        DialogEntity dialog = dialogEntityRepository.findById(data.getDialog()).get();
        Long userFirst = dialog.getUserFirst().getId();
        Long userSecond = dialog.getUserSecond().getId();
        String UUID = String.valueOf(client.getSessionId());
        if(clientHashRepository.findById(userFirst).isPresent()) {
            ClientHash clientHashFirst = clientHashRepository.findById(userFirst).get();
            if(clientHashFirst.getSessionId().equals(UUID)){
                if(userRepository.findById(userFirst).isPresent()) {
                    UserEntity userEntity = userRepository.findById(userFirst).get();
                    messageEntityRepository.messageViewed(userEntity, dialog);
                }
            }
        }
        if(clientHashRepository.findById(userSecond).isPresent()) {
            ClientHash clientHashSecond = clientHashRepository.findById(userSecond).get();
            if(clientHashSecond.getSessionId().equals(UUID)){
                if(userRepository.findById(userSecond).isPresent()) {
                    UserEntity userEntity = userRepository.findById(userSecond).get();
                    messageEntityRepository.messageViewed(userEntity, dialog);
                }
            }
        }
    }

    @OnEvent(value = START_TYPING)
    private void startTyping(SocketIOClient client, AckRequest ackSender, DialogDto data) {
        if (onLineDialogRepository.findById(data.getDialog()).isEmpty()) {
            DialogEntity dialogEntity = dialogEntityRepository.findById(data.getDialog()).get();
            OnLineDialog onLineDialog = new OnLineDialog();
            onLineDialog.setId(data.getDialog());
            List<Long> userIDList = new ArrayList<>();
            userIDList.add(dialogEntity.getUserFirst().getId());
            userIDList.add(dialogEntity.getUserSecond().getId());
            onLineDialog.setUsers(userIDList);
            onLineDialogRepository.save(onLineDialog);
        }
        sendEventTypeMassage(data.getDialog(), data.getAuthor());

    }

    @OnEvent(value = STOP_TYPING)
    private void stopTyping(SocketIOClient client, AckRequest ackSender, DialogDto data) {
        if (onLineDialogRepository.findById(data.getDialog()).isEmpty()) {
            DialogEntity dialogEntity = dialogEntityRepository.findById(data.getDialog()).get();
            OnLineDialog onLineDialog = new OnLineDialog();
            onLineDialog.setId(data.getDialog());
            List<Long> userIDList = new ArrayList<>();
            userIDList.add(dialogEntity.getUserFirst().getId());
            userIDList.add(dialogEntity.getUserSecond().getId());
            onLineDialog.setUsers(userIDList);
            onLineDialogRepository.save(onLineDialog);
        }
        sendEventStopTypeMassage(data.getDialog(), data.getAuthor());
    }

    private void sendEventStopTypeMassage(Long dialogId, Long authorId) {
        if (onLineDialogRepository.findById(dialogId).isPresent()) {
            OnLineDialog onLineDialog = onLineDialogRepository.findById(dialogId).get();
            onLineDialog.getUsers().forEach(userId -> {
                if (!authorId.equals(userId)) {
                    if (clientHashRepository.findById(userId).isPresent()) {
                        ClientHash clientHash = clientHashRepository.findById(userId).get();
                        SocketIOClient recipientClient = socketIOServer.getClient(UUID.fromString(clientHash.getSessionId()));
                        DialogDto dialogDto = new DialogDto();
                        dialogDto.setAuthor(userId);
                        dialogDto.setDialog(dialogId);
                        recipientClient.sendEvent(STOP_TYPING_RESPONSE, dialogDto);
                    }
                }
            });
        }
    }

    private void sendEventTypeMassage(Long dialogId, Long authorId) {
        if (onLineDialogRepository.findById(dialogId).isPresent()) {
            OnLineDialog onLineDialog = onLineDialogRepository.findById(dialogId).get();
            onLineDialog.getUsers().forEach(userId -> {
                if (!authorId.equals(userId)) {
                    if (clientHashRepository.findById(userId).isPresent()) {
                        ClientHash clientHash = clientHashRepository.findById(userId).get();
                        SocketIOClient recipientClient = socketIOServer.getClient(UUID.fromString(clientHash.getSessionId()));
                        TypingResponseDto typingResponseDto = new TypingResponseDto();
                        typingResponseDto.setAuthorId(userId);
                        typingResponseDto.setDialog(dialogId);
                        typingResponseDto.setAuthor(" ");
                        recipientClient.sendEvent(START_TYPING_RESPONSE, typingResponseDto);
                    }
                }
            });
        }
    }

}
