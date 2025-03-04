package com.danilo.roombooking.service.user;

import com.danilo.roombooking.config.security.SecurityConstants;
import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.role.Role;
import com.danilo.roombooking.domain.role.RoleType;
import com.danilo.roombooking.dto.UserRequestDTO;
import com.danilo.roombooking.repository.UserRepository;
import com.danilo.roombooking.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Value("${default-admin-username}")
    private String defaultAdminUsername;

    @Value("${default-admin-password}")
    private String defaultAdminPassword;

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

        if (userDTO.roles() != null) {
            Set<String> roleNames = userDTO.roles().stream()
                .map(RoleType::name)
                .collect(Collectors.toSet());
            List<Role> roles = roleService.getByNameIn(userDTO.roles());
            user.setRoles(new HashSet<>(roles));
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

    @Transactional
    public void createDefaultAdminIfNotExists() {
        if (userRepository.findByUsername(defaultAdminUsername).isPresent())
            return;

        UserRequestDTO defaultAdmin = new UserRequestDTO(
            defaultAdminUsername,
            defaultAdminPassword,
            "Admin",
            true,
            "admin@email.com",
            Set.of(RoleType.ROLE_ADMIN)
        );
        create(defaultAdmin);
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
