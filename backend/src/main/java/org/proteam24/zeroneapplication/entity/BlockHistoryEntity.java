package org.proteam24.zeroneapplication.entity;

import lombok.*;
import org.proteam24.zeroneapplication.entity.enumerated.Action;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BlockHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private UserEntity person;

    @Enumerated(EnumType.STRING)
    private Action action;
}
