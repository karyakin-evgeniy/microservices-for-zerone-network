package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
public class NotificationDto {

    @JsonProperty("sent_time")
    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant sentTime;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("entity_author")
    private UserDto entityAuthor;

    @JsonProperty("current_entity_id")
    private Long currentEntityId;

    private Long id;

    @JsonProperty("status")
    private int status;

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("parent_entity_id")
    private Long parentEntityId;

}
