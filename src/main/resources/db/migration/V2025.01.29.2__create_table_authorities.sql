CREATE TABLE Authorities
(
    UserId    BIGINT UNSIGNED NOT NULL,
    Authority VARCHAR(50)     NOT NULL,
    PRIMARY KEY(UserId, Authority),
    CONSTRAINT FK_Authorities_Users FOREIGN KEY (UserId) REFERENCES Users (Id)
);