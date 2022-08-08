package org.proteam24.zeroneapplication.entity;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "post")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String postText;

    private Boolean isBlocked;

    @OneToMany(mappedBy = "postEntity")
    private List<CommentEntity> commentEntities;

    private int likes = 0;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private List<Tag2PostEntity> tag2Posts;

    @Column(name = "is_deleted")
    private Boolean isDeleted = Boolean.FALSE;

    @UpdateTimestamp
    private LocalDateTime updated;
}
