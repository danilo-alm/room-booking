package com.danilo.roombooking.controller;

import com.danilo.roombooking.config.web.ApiPaths;
import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.dto.UserRequestDTO;
import com.danilo.roombooking.dto.UserResponseDTO;
import com.danilo.roombooking.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(ApiPaths.User.ROOT)
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operations related to user management")
public class UserController {

    private final UserService userService;

    @PostMapping(ApiPaths.User.CREATE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a user",
        description = "Creates a new user and returns the created resource with its generated ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User successfully created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
            content = @Content)
    })
    public ResponseEntity<UserResponseDTO> create(
        @Parameter(description = "User details", required = true)
        @RequestBody UserRequestDTO userDTO
    ) {
        UserResponseDTO user = new UserResponseDTO(userService.create(userDTO));
        URI loc = ServletUriComponentsBuilder.fromCurrentRequest()
            .path(ApiPaths.User.GET)
            .queryParam("id", user.id())
            .build().toUri();
        return ResponseEntity.created(loc).body(user);
    }

    @GetMapping(ApiPaths.User.GET)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get a user",
        description = "Retrieves a user by ID, username, or email. At least one parameter must be provided."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request, no parameters provided",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content)
    })
    public ResponseEntity<UserResponseDTO> getByIdOrUsernameOrEmail(
        @Parameter(description = "User ID", example = "1")
        @RequestParam(required = false) Long id,

        @Parameter(description = "Username", example = "john_doe")
        @RequestParam(required = false) String username,

        @Parameter(description = "Email", example = "john.doe@example.com")
        @RequestParam(required = false) String email
    ) {
        User user;
        if (id != null)
            user = userService.getById(id);
        else if (username != null)
            user = userService.getByUsername(username);
        else if (email != null)
            user = userService.getByEmail(email);
        else
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(new UserResponseDTO(user));
    }

    @DeleteMapping(ApiPaths.User.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a user",
        description = "Deletes a user by ID. This action is irreversible."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User successfully deleted",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content)
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID of the user to delete", required = true, example = "1")
        @PathVariable("id") Long id
    ) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
