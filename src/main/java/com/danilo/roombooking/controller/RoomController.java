package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.web.ApiPaths;
import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.domain.room.RoomType;
import com.danilo.roombooking.dto.RoomFilterDTO;
import com.danilo.roombooking.dto.RoomRequestDTO;
import com.danilo.roombooking.dto.RoomResponseDTO;
import com.danilo.roombooking.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(ApiPaths.Room.ROOT)
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping(ApiPaths.Room.CREATE)
    public ResponseEntity<RoomResponseDTO> create(@RequestBody RoomRequestDTO roomRequestDTO) {
        Room room = roomService.create(roomRequestDTO);
        return ResponseEntity.ok(new RoomResponseDTO(room));
    }

    @GetMapping(ApiPaths.Room.GET)
    public ResponseEntity<Page<RoomResponseDTO>> getAll(@PageableDefault Pageable pageable) {
        Page<Room> rooms = roomService.getAll(pageable);
        return ResponseEntity.ok(rooms.map(RoomResponseDTO::new));
    }

    @GetMapping(ApiPaths.Room.GET_BY_ID)
    public ResponseEntity<RoomResponseDTO> getById(@PathVariable Long id) {
        Room room = roomService.getById(id);
        return ResponseEntity.ok(new RoomResponseDTO(room));
    }

    @GetMapping(ApiPaths.Room.GET_BY_IDENTIFIER)
    public ResponseEntity<RoomResponseDTO> getByIdentifier(@PathVariable String identifier) {
        Room room = roomService.getByIdentifier(identifier);
        return ResponseEntity.ok(new RoomResponseDTO(room));
    }

    @GetMapping(ApiPaths.Room.GET_FILTER)
    public ResponseEntity<Page<RoomResponseDTO>> getFilter(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer minCapacity,
        @RequestParam(required = false) Integer maxCapacity,
        @RequestParam(required = false) RoomStatus status,
        @RequestParam(required = false) RoomType type,
        @RequestParam(required = false) Set<Long> amenityIds,
        @PageableDefault Pageable pageable
    ) {
        RoomFilterDTO roomFilterDTO = new RoomFilterDTO(name, minCapacity, maxCapacity, status, type, amenityIds);
        Page<Room> rooms = roomService.getFilter(roomFilterDTO, pageable);
        return ResponseEntity.ok(rooms.map(RoomResponseDTO::new));
    }

    @PutMapping(ApiPaths.Room.UPDATE)
    public ResponseEntity<RoomResponseDTO> update(
        @PathVariable("id") Long id,
        @RequestBody RoomRequestDTO roomRequestDTO
    ) {
        Room updatedRoom = roomService.update(id, roomRequestDTO);
        return ResponseEntity.ok(new RoomResponseDTO(updatedRoom));
    }

    @DeleteMapping(ApiPaths.Room.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiPaths.Room.GET_TYPES)
    public ResponseEntity<Map<String, String>> getRoomTypes() {
        return ResponseEntity.ok(RoomType.getMap());
    }

    @GetMapping(ApiPaths.Room.GET_STATUS)
    public ResponseEntity<Map<String, String>> getRoomStatus() {
        return ResponseEntity.ok(RoomStatus.getMap());
    }
}
