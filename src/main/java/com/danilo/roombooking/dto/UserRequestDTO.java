package com.danilo.roombooking.dto;

import java.util.Set;

public record UserRequestDTO(String username, String password, String fullName, Boolean enabled, String email,
                             Set<String> authorities) {
}
