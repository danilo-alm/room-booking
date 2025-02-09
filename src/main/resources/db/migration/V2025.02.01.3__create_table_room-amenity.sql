CREATE TABLE Room_Amenity
(
    RoomId BIGINT UNSIGNED,
    AmenityId BIGINT UNSIGNED,
    PRIMARY KEY (RoomId, AmenityId),
    CONSTRAINT FK_RoomAmenity_Room FOREIGN KEY (RoomId) REFERENCES Room (Id),
    CONSTRAINT FK_RoomAmenity_Amenity FOREIGN KEY (AmenityId) REFERENCES Amenity (Id)
);