package org.proteam24.zeroneapplication.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity postEntity;

    private Long parentId;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserEntity author;

    private String commentText;

    private Boolean isBlocked;

    private boolean isDeleted;

    private int likes;

}
