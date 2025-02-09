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
    public void UserRepository_FindById_ReturnsUser() {
        User user = createAndSaveUser("user", "user@example.com");
        Optional<User> foundUser = userRepository.findById(user.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
    }

    @Test
    public void UserRepository_FindUserByUsername_ReturnsUser() {
        User user = createAndSaveUser("user", "user@example.com");
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        assertTrue(foundUser.isPresent());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
    }

    @Test
    public void UserRepository_FindUserByEmail_ReturnsUser() {
        User user = createAndSaveUser("user", "user@example.com");
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());

        assertTrue(foundUser.isPresent());
        assertEquals(user.getUsername(), foundUser.get().getUsername());
    }

    @Test
    public void UserRepository_FindByUsername_ReturnsEmptyOptional() {
        Optional<User> user = userRepository.findByUsername("unknown");

        assertFalse(user.isPresent());
    }

    @Test
    public void UserRepository_FindByEmail_ReturnsEmptyOptional() {
        Optional<User> user = userRepository.findByEmail("unknown@example.com");

        assertFalse(user.isPresent());
    }

    @Test
    public void UserRepository_Save_UpdatesUser() {
        User user = createAndSaveUser("user", "user@example.com");
        user.setFailedLoginAttempts(3);
        userRepository.save(user);

        Optional<User> updatedUser = userRepository.findByUsername(user.getUsername());

        assertTrue(updatedUser.isPresent());
        assertEquals(3, updatedUser.get().getFailedLoginAttempts());
    }

    @Test
    public void UserRepository_DeleteById_DeletesUser() {
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
