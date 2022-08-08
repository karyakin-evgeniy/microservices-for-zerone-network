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
import org.proteam24.zeroneapplication.dto.socketdto.SocketUserDto;
import org.proteam24.zeroneapplication.entity.NotificationEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.entity.enumerated.NotificationType;

import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocketNotificationDto {

    private Long id;

    @JsonProperty("event_type")
    private String notificationType;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonProperty("sent_time")
    private Instant sentTime;

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("entity_author")
    private SocketUserDto entityAuthor;

    @JsonProperty("perent_id")
    private Long perentId;

    @JsonProperty("current_entity_id")
    private Long currentEntityId;

    public SocketNotificationDto(NotificationEntity notificationEntity, UserEntity userEntity, Long postId, Long commentId) {
        this.id = notificationEntity.getId();
        this.notificationType = String.valueOf(NotificationType.POST_COMMENT);
        this.sentTime = Instant.now();
        this.entityId = postId;
        this.entityAuthor = SocketUserDto.fromUser(userEntity);
        this.perentId = commentId;
        this.currentEntityId = commentId;

    }
}
