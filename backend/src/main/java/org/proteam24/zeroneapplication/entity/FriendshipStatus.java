package org.proteam24.zeroneapplication.entity;


import lombok.*;
import org.proteam24.zeroneapplication.entity.enumerated.FriendshipStatusCode;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class FriendshipStatus {

    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    private FriendshipStatusCode code;
}
