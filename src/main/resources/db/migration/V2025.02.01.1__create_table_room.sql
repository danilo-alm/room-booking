CREATE TABLE Room
(
    Id                  BIGINT UNSIGNED PRIMARY KEY,
    Identifier          VARCHAR(50)     NOT NULL UNIQUE,
    Name                VARCHAR(50)     NOT NULL,
    Description         VARCHAR(100),
    Capacity            INT             NOT NULL CHECK (Capacity > 0),
    Status              VARCHAR(50)     NOT NULL,
    Type                VARCHAR(50)     NOT NULL,
    CreatedAt           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE SEQUENCE room_sequence AS BIGINT INCREMENT BY 1 START WITH 1;