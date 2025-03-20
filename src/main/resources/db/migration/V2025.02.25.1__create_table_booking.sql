CREATE TABLE Booking
(
    Id              BIGINT UNSIGNED PRIMARY KEY,
    RoomId          BIGINT UNSIGNED NOT NULL,
    Approved        BOOLEAN NOT NULL DEFAULT FALSE,
    RequestedBy     BIGINT UNSIGNED NOT NULL,
    ApprovedBy      BIGINT UNSIGNED,
    StartTime       TIMESTAMP NOT NULL,
    EndTime         TIMESTAMP NOT NULL,
    CreatedAt       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT FK_Booking_Room FOREIGN KEY (RoomId) REFERENCES Room(Id),
    CONSTRAINT FK_Booking_UserRequested FOREIGN KEY (RequestedBy) REFERENCES Users(Id),
    CONSTRAINT FK_Booking_UserApproved FOREIGN KEY (ApprovedBy) REFERENCES Users(Id),
    CONSTRAINT UQ_Booking UNIQUE (RoomId, StartTime, EndTime)
);

CREATE INDEX IX_Booking ON Booking (RoomId, StartTime, EndTime);
CREATE INDEX IX_Booking_RequestedBy ON Booking (RequestedBy);
CREATE INDEX IX_Booking_ApprovedBy ON Booking (ApprovedBy);
CREATE INDEX IX_Booking_Approved ON Booking (Approved);
CREATE SEQUENCE booking_sequence AS BIGINT INCREMENT BY 1 START WITH 1;