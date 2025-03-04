package com.danilo.roombooking.service;

import com.danilo.roombooking.domain.privilege.Privilege;
import com.danilo.roombooking.domain.privilege.PrivilegeType;
import com.danilo.roombooking.repository.PrivilegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    @Transactional
    public void populateDatabase() {
        Set<PrivilegeType> existingPrivileges = privilegeRepository.findAll().stream()
            .map(Privilege::getName)
            .collect(Collectors.toSet());

        Set<Privilege> newPrivileges = EnumSet.allOf(PrivilegeType.class).stream()
            .filter(type -> !existingPrivileges.contains(type))
            .map(Privilege::new)
            .collect(Collectors.toSet());

        if (!newPrivileges.isEmpty())
            privilegeRepository.saveAll(newPrivileges);
    }
}
