package com.danilo.roombooking.repository.jpa;

import com.danilo.roombooking.domain.privilege.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeJpaRepository extends JpaRepository<Privilege, Long> {
}
