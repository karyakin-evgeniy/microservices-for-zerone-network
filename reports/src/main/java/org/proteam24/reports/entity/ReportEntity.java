package org.proteam24.reports.entity;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@RedisHash("ClientHash")
public class ReportEntity implements Serializable {
    @Id
    private Long id;

    private Long itemId;

    private Long userId;

    private String type;
}
