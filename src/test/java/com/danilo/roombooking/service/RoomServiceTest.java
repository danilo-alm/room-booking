package com.danilo.roombooking.service;

import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.domain.room.RoomType;
import com.danilo.roombooking.dto.RoomRequestDTO;
import com.danilo.roombooking.repository.RoomRepository;
import com.danilo.roombooking.service.amenity.AmenityService;
import com.danilo.roombooking.service.room.RoomNotFoundException;
import com.danilo.roombooking.service.room.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private AmenityService amenityService;

    @InjectMocks
    private RoomService roomService;

    Long roomId;
    Room room;
    RoomRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        roomId = 1L;

        requestDTO = createRoomRequestDTO("R101", "Classroom 101", 50);

        room = createRoom(1L, "R101", "Classroom 101", 50);
        room.setType(RoomType.SCIENCE_LAB);
    }

    @Test
    public void RoomService_Create_ReturnsCreatedRoom() {
        when(roomRepository.saveAndFlush(any(Room.class))).thenReturn(room);
        when(amenityService.getByIdIn(anyCollection())).thenReturn(List.of());

        Room response = roomService.create(requestDTO);

        assertNotNull(response);
        assertEquals("R101", response.getIdentifier());

        verify(roomRepository).saveAndFlush(any(Room.class));
    }

    @Test
    public void RoomService_GetById_ReturnsRoom() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        Room response = roomService.getById(roomId);

        assertNotNull(response);
        assertEquals("R101", response.getIdentifier());

        verify(roomRepository).findById(roomId);
    }

    @Test
    public void RoomService_GetByIdentifier_ReturnsRoom() {
        String identifier = room.getIdentifier();
        when(roomRepository.findByIdentifier(identifier)).thenReturn(Optional.of(room));

        Room response = roomService.getByIdentifier(identifier);

        assertNotNull(response);
        assertEquals("R101", response.getIdentifier());

        verify(roomRepository).findByIdentifier(identifier);
    }

    @Test
    public void RoomService_Update_ReturnsUpdatedRoom() {
        requestDTO = createRoomRequestDTO("R101", "Updated Classroom", 60);
        when(amenityService.getByIdIn(anyCollection())).thenReturn(List.of());

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        Room response = roomService.update(roomId, requestDTO);

        assertNotNull(response);
        assertEquals(60, response.getCapacity());

        verify(roomRepository).findById(roomId);
    }

    @Test
    public void RoomService_Update_ThrowsException_WhenRoomNotFound() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.update(roomId, requestDTO));

        verify(roomRepository).findById(roomId);
    }

    @Test
    public void RoomService_Delete_DeletesRoom() {
        when(roomRepository.existsById(roomId)).thenReturn(true);

        roomService.delete(roomId);

        verify(roomRepository).existsById(roomId);
        verify(roomRepository).deleteById(roomId);
    }

    @Test
    public void RoomService_Delete_ThrowsException_WhenRoomNotFound() {
        when(roomRepository.existsById(roomId)).thenReturn(false);

        assertThrows(RoomNotFoundException.class, () -> roomService.delete(roomId));

        verify(roomRepository).existsById(roomId);
        verify(roomRepository, never()).deleteById(roomId);
    }

    private Room createRoom(Long roomId, String identifier, String name, int capacity) {
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
            RoomStatus.AVAILABLE, RoomType.STANDARD_CLASSROOM, List.of(1L));
    }
}
