package com.danilo.roombooking.repository;

import com.danilo.roombooking.domain.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, BigInteger> {
    Optional<Room> findByIdentifier(String identifier);
}
