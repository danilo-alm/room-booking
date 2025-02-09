package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.ApiPaths;
import com.danilo.roombooking.dto.UserRequestDTO;
import com.danilo.roombooking.dto.UserResponseDTO;
import com.danilo.roombooking.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(ApiPaths.User.ROOT)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(ApiPaths.User.CREATE)
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userDTO) {
        UserResponseDTO user = userService.createUser(userDTO);
        URI loc = UriComponentsBuilder.fromPath(ApiPaths.User.GET)
            .queryParam("id", user.id())
            .build().toUri();
        return ResponseEntity.created(loc).body(user);
    }

    @GetMapping(ApiPaths.User.GET)
    public ResponseEntity<UserResponseDTO> getUser(
        @RequestParam(required = false) BigInteger id,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String email
    ) {
        Optional<UserResponseDTO> user = userService.getUser(id, username, email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping(ApiPaths.User.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") BigInteger id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
