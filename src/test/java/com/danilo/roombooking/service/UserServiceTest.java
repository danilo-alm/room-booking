package com.danilo.roombooking.service;

import com.danilo.roombooking.config.SecurityConstants;
import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.authority.Authority;
import com.danilo.roombooking.dto.UserRequestDTO;
import com.danilo.roombooking.repository.UserRepository;
import com.danilo.roombooking.service.user.InvalidUserException;
import com.danilo.roombooking.service.user.UserNotFoundException;
import com.danilo.roombooking.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            .id(1L)
            .username(userDTO.username())
            .password("encodedPassword")
            .fullName(userDTO.fullName())
            .email(userDTO.email())
            .enabled(userDTO.enabled())
            .authorities(userDTO.authorities().stream().map(val -> new Authority(user, val)).collect(Collectors.toSet()))
            .build();
    }

    @Test
    public void UserService_Create_CreatesUser() {
        when(passwordEncoder.encode(userDTO.password())).thenReturn("encodedPassword");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        User response = userService.create(userDTO);

        assertNotNull(response);
        assertEquals(userDTO.username(), response.getUsername());
        verify(userRepository).saveAndFlush(any(User.class));
        verify(passwordEncoder).encode(userDTO.password());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    public void UserService_Create_ThrowsException_WhenUsernameIsInvalid(String invalidUsername) {
        UserRequestDTO invalidUserDTO = new UserRequestDTO(invalidUsername, "password", "Test User", true, "test@example.com", Set.of("ROLE_USER"));

        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> userService.create(invalidUserDTO));

        assertEquals("username is required.", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    public void UserService_Create_ThrowsException_WhenPasswordIsInvalid(String invalidPassword) {
        UserRequestDTO invalidUserDTO = new UserRequestDTO("testUser", invalidPassword, "Test User", true, "test@example.com", Set.of("ROLE_USER"));

        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> userService.create(invalidUserDTO));

        assertEquals("password is required.", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideShortPasswords")
    public void UserService_Create_ThrowsException_WhenPasswordTooShort(String shortPassword) {
        UserRequestDTO invalidUserDTO = new UserRequestDTO("testUser", shortPassword, "Test User", true, "test@example.com", Set.of("ROLE_USER"));

        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> userService.create(invalidUserDTO));

        assertEquals("password must be at least " + SecurityConstants.MIN_PASSWORD_LENGTH + " characters.", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    public void UserService_Create_ThrowsException_WhenFullNameIsInvalid(String invalidFullName) {
        UserRequestDTO invalidUserDTO = new UserRequestDTO("testUser", "password", invalidFullName, true, "test@example.com", Set.of("ROLE_USER"));

        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> userService.create(invalidUserDTO));

        assertEquals("fullName is required.", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    public void UserService_Create_ThrowsException_WhenEmailIsInvalid(String invalidEmail) {
        UserRequestDTO invalidUserDTO = new UserRequestDTO("testUser", "password", "Test User", true, invalidEmail, Set.of("ROLE_USER"));

        InvalidUserException exception = assertThrows(InvalidUserException.class, () -> userService.create(invalidUserDTO));

        assertEquals("email is required.", exception.getMessage());
    }

    @Test
    public void UserService_GetById_ReturnsUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User response = userService.getById(user.getId());

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        verify(userRepository).findById(user.getId());
    }

    @Test
    public void UserService_GetById_ThrowsException_WhenUserNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getById(user.getId()));
    }

    @Test
    public void UserService_GetByUsername_ReturnsUser() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        User response = userService.getByUsername(user.getUsername());

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    public void UserService_GetByUsername_ThrowsException_WhenUserNotFound() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getByUsername(user.getUsername()));
    }

    @Test
    public void UserService_GetByEmail_ReturnsUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        User response = userService.getByEmail(user.getEmail());

        assertNotNull(response);
        assertEquals(user.getEmail(), response.getEmail());
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    public void UserService_Delete_DeletesUser() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        doNothing().when(userRepository).deleteById(user.getId());

        assertDoesNotThrow(() -> userService.delete(user.getId()));
        verify(userRepository).existsById(user.getId());
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    public void UserService_Delete_ThrowsException_WhenUserNotFound() {
        when(userRepository.existsById(any())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.delete(user.getId()));

        verify(userRepository).existsById(any());
        verify(userRepository, never()).deleteById(any());
    }

    static Stream<String> provideShortPasswords() {
        return Stream.of("123", "abcd", "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH - 1));
    }
}
