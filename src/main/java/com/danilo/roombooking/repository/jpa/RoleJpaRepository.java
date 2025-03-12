package com.danilo.roombooking.repository.jpa;

import com.danilo.roombooking.domain.role.Role;
import com.danilo.roombooking.domain.role.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RoleJpaRepository extends JpaRepository<Role, Long> {
    List<Role> findByNameIn(Collection<RoleType> names);
}
