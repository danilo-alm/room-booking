package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.ApiPaths;
import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.dto.UserRequestDTO;
import com.danilo.roombooking.dto.UserResponseDTO;
import com.danilo.roombooking.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(ApiPaths.User.ROOT)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(ApiPaths.User.CREATE)
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserRequestDTO userDTO) {
        UserResponseDTO user = new UserResponseDTO(userService.create(userDTO));
        URI loc = UriComponentsBuilder.fromPath(ApiPaths.User.GET)
            .queryParam("id", user.id())
            .build().toUri();
        return ResponseEntity.created(loc).body(user);
    }

    @GetMapping(ApiPaths.User.GET)
    public ResponseEntity<UserResponseDTO> getByIdOrUsernameOrEmail(
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String email
    ) {
        User user;
        if (id != null) {
            user = userService.getById(id);
        } else if (username != null) {
            user = userService.getByUsername(username);
        } else if (email != null) {
            user = userService.getByEmail(email);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new UserResponseDTO(user));
    }

    @DeleteMapping(ApiPaths.User.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
