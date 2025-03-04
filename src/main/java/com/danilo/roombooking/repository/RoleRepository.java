package com.danilo.roombooking.repository;

import com.danilo.roombooking.domain.role.Role;
import com.danilo.roombooking.domain.role.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByNameIn(Collection<RoleType> names);
}
