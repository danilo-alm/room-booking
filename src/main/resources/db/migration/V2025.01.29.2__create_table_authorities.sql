CREATE TABLE Authorities
(
    UserId    BIGINT UNSIGNED NOT NULL,
    Authority VARCHAR(50)     NOT NULL,
    CONSTRAINT FK_Authorities_Users FOREIGN KEY (UserId) REFERENCES Users (Id)
);

CREATE UNIQUE INDEX UX_Authorities_UserId_Authority ON Authorities (UserId, Authority);
