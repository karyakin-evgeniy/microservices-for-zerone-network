package org.proteam24.zeroneapplication.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "dialog")
@NoArgsConstructor
public class DialogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_first")
    private UserEntity userFirst;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_second")
    private UserEntity userSecond;

    public DialogEntity(UserEntity user, UserEntity userRecipient) {
        this.userFirst = user;
        this.userSecond = userRecipient;
    }
}
