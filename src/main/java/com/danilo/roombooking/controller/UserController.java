package com.danilo.roombooking.controller;

import com.danilo.roombooking.dto.UserRequestDTO;
import com.danilo.roombooking.dto.UserResponseDTO;
import com.danilo.roombooking.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.net.URI;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDTO> getUser(
        @RequestParam(required = false) BigInteger id,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String email
    ) {
        if (id != null) {
            return ResponseEntity.ok(userService.getById(id));
        }
        if (username != null) {
            return ResponseEntity.ok(userService.getByUsername(username));
        }
        if (email != null) {
            return ResponseEntity.ok(userService.getByEmail(email));
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userDTO) {
        UserResponseDTO user = userService.createUser(userDTO);
        URI loc = URI.create("/user/" + user.id());
        return ResponseEntity.created(loc).body(user);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestParam BigInteger id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
