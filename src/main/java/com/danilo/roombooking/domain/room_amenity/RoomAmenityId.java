package com.danilo.roombooking.domain.room_amenity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomAmenityId implements Serializable {
    private BigInteger room;
    private BigInteger amenity;
}
