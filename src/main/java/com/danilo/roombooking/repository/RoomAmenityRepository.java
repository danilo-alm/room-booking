package com.danilo.roombooking.repository;

import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room_amenity.RoomAmenity;
import com.danilo.roombooking.domain.room_amenity.RoomAmenityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Set;

public interface RoomAmenityRepository extends JpaRepository<RoomAmenity, RoomAmenityId> {
    @Query("SELECT ra.room FROM RoomAmenity ra WHERE ra.amenity.id = :amenityId")
    Set<Room> findRoomsByAmenityId(@Param("amenityId") BigInteger amenityId);

    @Query("SELECT ra.room FROM RoomAmenity ra WHERE ra.amenity.id IN :amenitiesIds")
    Set<Room> findRoomsByAmenitiesIds(@Param("amenitiesIds") Set<BigInteger> amenitiesIds);
}
