package com.danilo.roombooking.repository;

import com.danilo.roombooking.domain.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigInteger;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, BigInteger>, JpaSpecificationExecutor<Room> {
    Optional<Room> findByIdentifier(String identifier);
}
