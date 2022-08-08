package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.proteam24.zeroneapplication.entity.MessageEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.entity.enumerated.ReadStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;

    @JsonProperty("dialog_id")
    private Long dialogId;

    private LocalDateTime time;

    @JsonProperty("author_id")
    private Long authorId;

    private Boolean sendByMe;

    @JsonProperty("message_text")
    private String messageText;

    @JsonProperty("read_status")
    private int readStatus;

    public MessageDto(MessageEntity messageEntity) {
        this.id = messageEntity.getId();
        this.time = messageEntity.getTime();
        this.authorId = messageEntity.getRecipient().getId();
        this.sendByMe = false;
        this.readStatus = messageEntity.getReadStatus();
        this.messageText = messageEntity.getMessageText();
        this.dialogId = messageEntity.getDialog().getId();
    }
}
