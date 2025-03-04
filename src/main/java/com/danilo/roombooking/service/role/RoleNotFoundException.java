package com.danilo.roombooking.service.role;

import com.danilo.roombooking.domain.role.RoleType;

import java.util.Collection;

public class RoleNotFoundException extends RuntimeException {
  public RoleNotFoundException(String role) {
    super("role not found: " + role);
  }

  public RoleNotFoundException(Collection<RoleType> roles) {
    super("roles not found: " + roles);
  }
}
