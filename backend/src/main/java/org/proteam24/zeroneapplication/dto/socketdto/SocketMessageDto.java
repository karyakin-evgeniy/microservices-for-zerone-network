package org.proteam24.zeroneapplication.dto.socketdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.proteam24.zeroneapplication.entity.MessageEntity;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocketMessageDto {
    private Long id;

    @JsonProperty("dialog_id")
    private Long dialogId;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant time;

    @JsonProperty("author_id")
    private Long authorId;

    private Boolean sendByMe;

    @JsonProperty("message_text")
    private String messageText;

    @JsonProperty("read_status")
    private int readStatus;

    public SocketMessageDto(MessageEntity messageEntity) {
        this.id = messageEntity.getId();
        this.time = Instant.now();
        this.authorId = messageEntity.getRecipient().getId();
        this.sendByMe = false;
        this.readStatus = messageEntity.getReadStatus();
        this.messageText = messageEntity.getMessageText();
        this.dialogId = messageEntity.getDialog().getId();
    }
}
