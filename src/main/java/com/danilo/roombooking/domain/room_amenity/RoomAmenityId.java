package com.danilo.roombooking.domain.room_amenity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomAmenityId implements Serializable {
    private Long room;
    private Long amenity;
}
