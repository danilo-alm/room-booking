CREATE TABLE Users
(
    Id                  BIGINT UNSIGNED PRIMARY KEY,
    Username            VARCHAR(36)  NOT NULL UNIQUE,
    Password            CHAR(60)     NOT NULL,
    Enabled             BOOLEAN      NOT NULL DEFAULT TRUE,
    Locked              BOOLEAN      NOT NULL DEFAULT FALSE,
    Email               VARCHAR(254) NOT NULL UNIQUE,
    FullName            VARCHAR(100) NOT NULL,

    CreatedAt           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    LastLogin           TIMESTAMP,

    FailedLoginAttempts INT          NOT NULL DEFAULT 0
);

CREATE SEQUENCE user_sequence AS BIGINT INCREMENT BY 1 START WITH 1;