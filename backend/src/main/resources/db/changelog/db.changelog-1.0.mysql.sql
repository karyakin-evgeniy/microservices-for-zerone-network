-- liquibase formatted sql

-- changeset Ilia:1
CREATE TABLE IF NOT EXISTS users
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    created             datetime     NULL,
    status              VARCHAR(255) NULL,
    updated             datetime     NULL,
    about               VARCHAR(255) NULL,
    city                VARCHAR(255) NULL,
    country             VARCHAR(255) NULL,
    birth_date          datetime     NULL,
    confirmation_code   VARCHAR(255) NULL,
    email               VARCHAR(255) NULL,
    first_name          VARCHAR(255) NOT NULL,
    is_approved         BIT(1)       NULL,
    is_blocked          BIT(1)       NULL,
    last_name           VARCHAR(255) NOT NULL,
    last_online_time    datetime     NULL,
    messages_permission VARCHAR(255) NULL,
    password            VARCHAR(255) NULL,
    phone               VARCHAR(255) NULL,
    photo               VARCHAR(255) NULL,
    reg_date            datetime     NULL,
    deleted             TINYINT(1) DEFAULT 0
);

-- changeset Ilia:2
CREATE TABLE IF NOT EXISTS post
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    is_blocked BIT(1)       NULL,
    is_deleted BIT(1)       NULL,
    likes      INT          NOT NULL,
    post_text  VARCHAR(255) NULL,
    tags       TINYBLOB     NULL,
    time       datetime     NULL,
    title      VARCHAR(255) NULL,
    author_id  BIGINT       NULL,
    FOREIGN KEY (author_id) REFERENCES users (id)
);


-- changeset Ilia:3
CREATE TABLE IF NOT EXISTS file
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    `path`  VARCHAR(255) NULL,
    post_id BIGINT       NULL,
    FOREIGN KEY (post_id) REFERENCES post (id)
);

-- changeset Ilia:4
CREATE TABLE IF NOT EXISTS friendship
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    code          VARCHAR(255) NULL,
    time          datetime     NULL,
    dst_person_id BIGINT       NULL,
    src_person_id BIGINT       NULL,
    FOREIGN KEY (dst_person_id) REFERENCES users (id),
    FOREIGN KEY (src_person_id) REFERENCES users (id)
);

-- changeset Ilia:5
CREATE TABLE IF NOT EXISTS likes
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    entity_id BIGINT       NULL,
    time      datetime     NULL,
    type      VARCHAR(255) NULL,
    person_id BIGINT       NULL,
    post_id   BIGINT       NULL,
    FOREIGN KEY (person_id) REFERENCES users (id),
    FOREIGN KEY (post_id) REFERENCES post (id)
);

-- changeset Ilia:6
CREATE TABLE IF NOT EXISTS message
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    message_text VARCHAR(255) NULL,
    read_status  INT          NULL,
    time         datetime     NULL,
    author_id    BIGINT       NULL,
    recipient_id BIGINT       NULL,
    FOREIGN KEY (author_id) REFERENCES users (id),
    FOREIGN KEY (recipient_id) REFERENCES users (id)
);

-- changeset Ilia:7
CREATE TABLE IF NOT EXISTS notification
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    notification_type INT      NULL,
    sent_time         datetime NULL,
    person_id         BIGINT   NULL,
    FOREIGN KEY (person_id) REFERENCES users (id)
);

-- changeset Ilia:8
CREATE TABLE IF NOT EXISTS comment
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    comment_text VARCHAR(255) NULL,
    is_blocked   BIT(1)       NULL,
    is_deleted   BIT(1)       NOT NULL,
    likes        INT          NOT NULL,
    parent_id    BIGINT       NULL,
    time         datetime     NULL,
    author_id    BIGINT       NULL,
    post_id      BIGINT       NULL,
    FOREIGN KEY (author_id) REFERENCES users (id),
    FOREIGN KEY (post_id) REFERENCES post (id)
);


-- changeset Ilia:9
CREATE TABLE IF NOT EXISTS roles
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    created datetime     NULL,
    status  VARCHAR(255) NULL,
    updated datetime     NULL,
    name    VARCHAR(255) NULL
);

-- changeset Ilia:10
CREATE TABLE IF NOT EXISTS tag
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

-- changeset Ilia:11
CREATE TABLE IF NOT EXISTS tag2post
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NULL,
    tag_id  BIGINT NULL,
    FOREIGN KEY (post_id) REFERENCES post (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id)
);

-- changeset Ilia:12
CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- changeset Ilia:13
CREATE TABLE IF NOT EXISTS users_message
(
    user_id    BIGINT        NOT NULL,
    message_id BIGINT UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (message_id) REFERENCES message (id)
);

-- changeset Ilia:14
CREATE TABLE IF NOT EXISTS block_history
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    action    VARCHAR(255) NULL,
    time      datetime     NULL,
    person_id BIGINT       NULL,
    FOREIGN KEY (person_id) REFERENCES users (id)
);

-- changeset Ilia:15
CREATE TABLE IF NOT EXISTS users_friendship
(
    user_id       BIGINT        NOT NULL,
    friendship_id BIGINT UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friendship_id) REFERENCES friendship (id)
);