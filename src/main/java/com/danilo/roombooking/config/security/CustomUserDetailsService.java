package com.danilo.roombooking.config.security;

import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.role.Role;
import com.danilo.roombooking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new
            UsernameNotFoundException(username));

        return new CustomUserDetails(
            user.getId(), user.getUsername(), user.getPassword(),
            user.getEnabled(), user.getLocked(), extractAuthorities(user)
        );
    }

    private Set<GrantedAuthority> extractAuthorities(User user) {
        return user.getRoles().stream()
            .flatMap(role -> Stream.concat(
                Stream.of(new SimpleGrantedAuthority(role.getName().name())),
                role.getName().getPrivileges().stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.name()))
            ))
            .collect(Collectors.toSet());
    }
}