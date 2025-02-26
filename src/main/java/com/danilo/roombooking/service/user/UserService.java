package com.danilo.roombooking.service.user;

import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.authority.Authority;
import com.danilo.roombooking.dto.UserRequestDTO;
import com.danilo.roombooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(UserRequestDTO userDTO) {
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

    public User getById(BigInteger id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void deleteUser(BigInteger id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);
    }
}
