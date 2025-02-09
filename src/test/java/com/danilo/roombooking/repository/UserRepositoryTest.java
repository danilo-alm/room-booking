package com.danilo.roombooking.repository;

import com.danilo.roombooking.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class UserRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveAndRetrieveUser() {
        createAndSaveUser("user", "user@example.com");
        Optional<User> foundUser = userRepository.findByUsername("user");

        assertTrue(foundUser.isPresent());
        assertEquals("user@example.com", foundUser.get().getEmail());
    }

    @Test
    public void shouldFindUserByEmail() {
        User user = createAndSaveUser("user", "user@example.com");
        Optional<User> foundUser = userRepository.findByEmail("user@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("user", foundUser.get().getUsername());
    }

    @Test
    public void shouldNotFindNonExistentUser() {
        Optional<User> user = userRepository.findByUsername("unknown");

        assertFalse(user.isPresent());
    }

    @Test
    public void shouldUpdateFailedLoginAttempts() {
        User user = createAndSaveUser("user", "user@example.com");
        user.setFailedLoginAttempts(3);
        userRepository.save(user);

        Optional<User> updatedUser = userRepository.findByUsername("user");

        assertTrue(updatedUser.isPresent());
        assertEquals(3, updatedUser.get().getFailedLoginAttempts());
    }

    @Test
    public void shouldDeleteUser() {
        User user = createAndSaveUser("user", "user@example.com");
        userRepository.deleteById(user.getId());

        Optional<User> deletedUser = userRepository.findById(user.getId());

        assertFalse(deletedUser.isPresent());
    }

    private User createAndSaveUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("securepassword");
        user.setEmail(email);
        user.setFullName("Test User");
        user.setEnabled(true);
        user.setFailedLoginAttempts(0);

        return userRepository.save(user);
    }
}
