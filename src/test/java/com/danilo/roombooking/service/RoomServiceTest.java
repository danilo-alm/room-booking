package com.danilo.roombooking.service;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.domain.room.RoomType;
import com.danilo.roombooking.dto.RoomRequestDTO;
import com.danilo.roombooking.dto.RoomResponseDTO;
import com.danilo.roombooking.repository.RoomRepository;
import com.danilo.roombooking.service.amenity.AmenityService;
import com.danilo.roombooking.service.room.RoomNotFoundException;
import com.danilo.roombooking.service.room.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private AmenityService amenityService;

    @InjectMocks
    private RoomService roomService;

    private Room createRoom(BigInteger roomId, String identifier, String name, int capacity) {
        Room room = new Room();
        room.setId(roomId);
        room.setIdentifier(identifier);
        room.setName(name);
        room.setDescription("A large classroom");
        room.setCapacity(capacity);
        room.setType(RoomType.STANDARD_CLASSROOM);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setAmenities(new HashSet<>());
        return room;
    }

    private RoomRequestDTO createRoomRequestDTO(String identifier, String name, int capacity) {
        return new RoomRequestDTO(identifier, name, "A large classroom", capacity,
            RoomStatus.AVAILABLE, RoomType.STANDARD_CLASSROOM, List.of(BigInteger.ONE));
    }

    private Amenity createAmenity(BigInteger amenityId) {
        Amenity amenity = new Amenity();
        amenity.setId(amenityId);
        return amenity;
    }

    @Test
    public void RoomService_CreateRoom_ReturnsCreatedRoom() {
        RoomRequestDTO requestDTO = createRoomRequestDTO("R101", "Classroom 101", 50);
        Room room = createRoom(BigInteger.ONE, "R101", "Classroom 101", 50);

        when(roomRepository.saveAndFlush(any(Room.class))).thenReturn(room);

        RoomResponseDTO response = roomService.createRoom(requestDTO);

        assertNotNull(response);
        assertEquals("R101", response.identifier());
        assertEquals("Classroom 101", response.name());
        assertEquals(50, response.capacity());

        verify(roomRepository).saveAndFlush(any(Room.class));
    }

    @Test
    public void RoomService_GetRoom_ById_ReturnsRoom() {
        BigInteger roomId = BigInteger.ONE;
        Room room = createRoom(roomId, "R101", "Classroom 101", 50);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        Optional<RoomResponseDTO> response = roomService.getRoom(roomId, null);

        assertTrue(response.isPresent());
        assertEquals("R101", response.get().identifier());
        assertEquals("Classroom 101", response.get().name());

        verify(roomRepository).findById(roomId);
    }

    @Test
    public void RoomService_GetRoom_ByIdentifier_ReturnsRoom() {
        String identifier = "R101";
        Room room = createRoom(BigInteger.ONE, identifier, "Classroom 101", 50);

        when(roomRepository.findByIdentifier(identifier)).thenReturn(Optional.of(room));

        Optional<RoomResponseDTO> response = roomService.getRoom(null, identifier);

        assertTrue(response.isPresent());
        assertEquals("R101", response.get().identifier());
        assertEquals("Classroom 101", response.get().name());

        verify(roomRepository).findByIdentifier(identifier);
    }

    @Test
    public void RoomService_UpdateRoom_ReturnsUpdatedRoom() {
        BigInteger roomId = BigInteger.ONE;
        RoomRequestDTO requestDTO = createRoomRequestDTO("R101", "Updated Classroom", 60);
        Amenity amenity = createAmenity(BigInteger.ONE);
        Room room = createRoom(roomId, "R101", "Classroom 101", 50);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(amenityService.getAmenitiesByIds(anyCollection())).thenReturn(List.of(amenity));

        RoomResponseDTO response = roomService.updateRoom(roomId, requestDTO);

        assertNotNull(response);
        assertEquals("R101", response.identifier());
        assertEquals("Updated Classroom", response.name());
        assertEquals(60, response.capacity());

        verify(roomRepository).findById(roomId);
    }

    @Test
    public void RoomService_UpdateRoom_ThrowsException_WhenRoomNotFound() {
        BigInteger roomId = BigInteger.ONE;
        RoomRequestDTO requestDTO = createRoomRequestDTO("R101", "Updated Classroom", 60);

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.updateRoom(roomId, requestDTO));

        verify(roomRepository).findById(roomId);
    }

    @Test
    public void RoomService_DeleteRoom_DeletesRoom() {
        BigInteger roomId = BigInteger.ONE;

        when(roomRepository.existsById(roomId)).thenReturn(true);

        roomService.deleteRoom(roomId);

        verify(roomRepository).deleteById(roomId);
    }

    @Test
    public void RoomService_DeleteRoom_ThrowsException_WhenRoomNotFound() {
        BigInteger roomId = BigInteger.ONE;

        when(roomRepository.existsById(roomId)).thenReturn(false);

        assertThrows(RoomNotFoundException.class, () -> roomService.deleteRoom(roomId));

        verify(roomRepository, never()).deleteById(roomId);
    }
}
