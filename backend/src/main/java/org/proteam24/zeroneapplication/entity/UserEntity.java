package org.proteam24.zeroneapplication.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.proteam24.zeroneapplication.entity.enumerated.MessagesPermission;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE users SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted=false")
@Table(name = "users")
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private LocalDateTime regDate;

    private LocalDateTime birthDate;

    private String phone;

    private String city;

    private String country;

    private String photo;

    private String about;

    private String confirmationCode;

    private Boolean isApproved;

    @Enumerated(EnumType.STRING)
    private MessagesPermission messagesPermission;

    private LocalDateTime lastOnlineTime;

    private Boolean isBlocked;

    private Boolean deleted = Boolean.FALSE;

    @OneToMany(mappedBy = "person")
    private List<BlockHistoryEntity> blockHistories;

    @OneToMany
    private List<FriendshipEntity> friendshipEntities;

    @OneToMany
    private List<MessageEntity> messageEntities;

    @OneToMany(mappedBy = "author")
    private List<PostEntity> postEntities;

    @OneToMany(mappedBy = "author")
    private List<CommentEntity> commentEntities;

    @OneToMany(mappedBy = "userEntity")
    private List<Support> supports;

    public LocalDateTime getBirthDate() {
        return birthDate == null ? LocalDate.of(2000, 1, 1).atStartOfDay() : birthDate;
    }
}
