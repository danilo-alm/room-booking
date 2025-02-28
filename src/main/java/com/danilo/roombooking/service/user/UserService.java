package com.danilo.roombooking.service.user;

import com.danilo.roombooking.config.SecurityConstants;
import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.authority.Authority;
import com.danilo.roombooking.dto.UserRequestDTO;
import com.danilo.roombooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User create(UserRequestDTO userDTO) {
        validateUserRequest(userDTO);

        String encryptedPassword = passwordEncoder.encode(userDTO.password());

        User user = User.builder()
            .username(userDTO.username())
            .password(encryptedPassword)
            .fullName(userDTO.fullName())
            .email(userDTO.email())
            .enabled(userDTO.enabled())
            .build();

        if (userDTO.authorities() != null) {
            user.setAuthorities(userDTO.authorities().stream().map(val ->
                new Authority(user, val)).collect(Collectors.toSet()));
        }

        return userRepository.saveAndFlush(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);
    }

    private void validateUserRequest(UserRequestDTO userRequestDTO) {
        if (userRequestDTO.username() == null || userRequestDTO.username().isBlank())
            throw new InvalidUserException("username is required.");

        if (userRequestDTO.password() == null || userRequestDTO.password().isBlank())
            throw new InvalidUserException("password is required.");

        if (userRequestDTO.password().length() < SecurityConstants.MIN_PASSWORD_LENGTH)
            throw new InvalidUserException("password must be at least "
                + SecurityConstants.MIN_PASSWORD_LENGTH + " characters.");

        if (userRequestDTO.fullName() == null || userRequestDTO.fullName().isBlank())
            throw new InvalidUserException("fullName is required.");

        if (userRequestDTO.email() == null || userRequestDTO.email().isBlank())
            throw new InvalidUserException("email is required.");
    }

}
