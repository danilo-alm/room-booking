CREATE TABLE Room
(
    Id                  BIGINT UNSIGNED AUTO_INCREAMENT PRIMARY KEY,
    Identifier          VARCHAR(50)     NOT NULL UNIQUE,
    Name                VARCHAR(50)     NOT NULL,
    Description         VARCHAR(100),
    Capacity            INT             NOT NULL,
    Status              VARCHAR(50)     NOT NULL,
    CreatedAt           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX IX_Room_Identifier ON Room (Identifier);