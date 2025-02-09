package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.ApiPaths;
import com.danilo.roombooking.domain.room.RoomType;
import com.danilo.roombooking.dto.RoomRequestDTO;
import com.danilo.roombooking.dto.RoomResponseDTO;
import com.danilo.roombooking.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(ApiPaths.Room.ROOT)
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping(ApiPaths.Room.CREATE)
    public ResponseEntity<RoomResponseDTO> createRoom(@RequestBody  RoomRequestDTO roomRequestDTO) {
        return ResponseEntity.ok(roomService.createRoom(roomRequestDTO));
    }

    @GetMapping(ApiPaths.Room.GET)
    public ResponseEntity<RoomResponseDTO> getRoom(
        @RequestParam(required = false) BigInteger id,
        @RequestParam(required = false) String identifier
    ) {
        Optional<RoomResponseDTO> roomResponseDTO = roomService.getRoom(id, identifier);
        return roomResponseDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping(ApiPaths.Room.UPDATE)
    public ResponseEntity<RoomResponseDTO> updateRoom(@PathVariable("id") BigInteger id,
                                                      @RequestBody RoomRequestDTO roomRequestDTO) {
        return ResponseEntity.ok(roomService.updateRoom(id, roomRequestDTO));
    }

    @DeleteMapping(ApiPaths.Room.DELETE)
    public ResponseEntity<Void> deleteRoom(@PathVariable BigInteger id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiPaths.Room.GET_TYPES)
    public ResponseEntity<Map<String, String>> getRoomTypes() {
        return ResponseEntity.ok(RoomType.getMap());
    }
}
