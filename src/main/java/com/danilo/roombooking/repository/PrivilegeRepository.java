package com.danilo.roombooking.repository;

import com.danilo.roombooking.domain.privilege.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
}
