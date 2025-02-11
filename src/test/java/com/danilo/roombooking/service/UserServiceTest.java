package com.danilo.roombooking.service;

import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.authority.Authority;
import com.danilo.roombooking.dto.UserRequestDTO;
import com.danilo.roombooking.dto.UserResponseDTO;
import com.danilo.roombooking.repository.UserRepository;
import com.danilo.roombooking.service.user.UserNotFoundException;
import com.danilo.roombooking.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO userDTO;
    private User user;

    @BeforeEach
    public void setUp() {
        userDTO = new UserRequestDTO("testUser", "password", "Test User", true, "test@example.com", Set.of("ROLE_USER"));
        user = User.builder()
            .id(BigInteger.ONE)
            .username(userDTO.username())
            .password("encodedPassword")
            .fullName(userDTO.fullName())
            .email(userDTO.email())
            .enabled(userDTO.enabled())
            .authorities(userDTO.authorities().stream().map(val -> new Authority(user, val)).collect(Collectors.toSet()))
            .build();
    }

    @Test
    public void UserService_CreateUser_CreatesUser() {
        when(passwordEncoder.encode(userDTO.password())).thenReturn("encodedPassword");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.createUser(userDTO);

        assertNotNull(response);
        assertEquals(user.getUsername(), response.username());
        assertEquals(user.getEmail(), response.email());
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    public void UserService_GetUser_UsernameParam_ReturnsUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Optional<UserResponseDTO> response = userService.getUser(user.getId(), null, null);

        assertTrue(response.isPresent());
        assertEquals(user.getUsername(), response.get().username());
    }

    @Test
    public void UserService_GetUser_IdParam_ReturnsUser() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        Optional<UserResponseDTO> response = userService.getUser(null, user.getUsername(), null);

        assertTrue(response.isPresent());
        assertEquals(user.getEmail(), response.get().email());
    }

    @Test
    public void UserService_GetUser_EmailParam_ReturnsUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Optional<UserResponseDTO> response = userService.getUser(null, null, user.getEmail());

        assertTrue(response.isPresent());
        assertEquals(user.getUsername(), response.get().username());
    }

    @Test
    public void UserService_GetUser_MultipleParams_ReturnsEmpty() {
        Optional<UserResponseDTO> response = userService.getUser(BigInteger.ONE, "testUser", "test@example.com");
        assertFalse(response.isPresent());
    }

    @Test
    public void UserService_DeleteById_DeletesUser() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        doNothing().when(userRepository).deleteById(user.getId());

        assertDoesNotThrow(() -> userService.deleteUser(user.getId()));
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    void UserService_DeleteById_ThrowsException() {
        when(userRepository.existsById(user.getId())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(user.getId()));
    }
}
