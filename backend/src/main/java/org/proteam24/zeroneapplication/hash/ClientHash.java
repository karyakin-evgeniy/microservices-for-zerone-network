package org.proteam24.zeroneapplication.hash;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@RedisHash("ClientHash")
public class ClientHash implements Serializable {

    @Id
    private Long userId;

    private String sessionId;
}
