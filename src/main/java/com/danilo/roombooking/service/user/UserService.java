package com.danilo.roombooking.service.user;

import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.authority.Authority;
import com.danilo.roombooking.dto.UserRequestDTO;
import com.danilo.roombooking.dto.UserResponseDTO;
import com.danilo.roombooking.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    public UserResponseDTO getById(BigInteger id) {
        User foundUser = userRepository.findById(id).orElseThrow(
            UserNotFoundException::new
        );
        return new UserResponseDTO(foundUser);
    }

    public UserResponseDTO getByUsername(String username) {
        User foundUser = userRepository.findByUsername(username).orElseThrow(
            UserNotFoundException::new
        );
        return new UserResponseDTO(foundUser);
    }

    public UserResponseDTO getByEmail(String email) {
        User foundUser = userRepository.findByEmail(email).orElseThrow(
            UserNotFoundException::new
        );
        return new UserResponseDTO(foundUser);
    }

    public void deleteUser(BigInteger id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userDTO) {
        String encryptedPassword = passwordEncoder.encode(userDTO.password());

        User user = User.builder()
            .username(userDTO.username())
            .password(encryptedPassword)
            .fullName(userDTO.fullName())
            .email(userDTO.email())
            .enabled(userDTO.enabled())
            .build();

        userRepository.saveAndFlush(user);
        entityManager.refresh(user);

        if (userDTO.authorities() != null) {
            for (String value : userDTO.authorities()) {
                Authority authority = new Authority();
                authority.setAuthority(value);
                authority.setUser(user);
                user.addAuthority(authority);
            }
        }

        userRepository.save(user);
        return new UserResponseDTO(user);
    }
}
