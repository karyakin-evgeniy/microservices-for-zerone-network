package org.proteam24.zeroneapplication.hash;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@RedisHash("OnLineDialog")
public class OnLineDialog implements Serializable {
    @Id
    private Long id;

    private List<Long> users;
}
