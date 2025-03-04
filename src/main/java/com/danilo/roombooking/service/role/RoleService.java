package com.danilo.roombooking.service.role;

import com.danilo.roombooking.domain.privilege.Privilege;
import com.danilo.roombooking.domain.privilege.PrivilegeType;
import com.danilo.roombooking.domain.role.Role;
import com.danilo.roombooking.domain.role.RoleType;
import com.danilo.roombooking.repository.PrivilegeRepository;
import com.danilo.roombooking.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    public List<Role> getByNameIn(Collection<RoleType> roles) {
        List<Role> foundRoles = roleRepository.findByNameIn(roles);
        if (foundRoles.size() == roles.size())
            return foundRoles;

        List<RoleType> rolesNotFound = roles.stream()
            .filter(name -> !foundRoles.contains(name))
            .toList();

        throw new RoleNotFoundException(rolesNotFound);
    }

    @Transactional
    public void populateDatabase() {
        Set<RoleType> existingRoles = roleRepository.findAll().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());

        Set<Privilege> allPrivileges = new HashSet<>(privilegeRepository.findAll());

        Map<PrivilegeType, Privilege> privilegeMap = allPrivileges.stream()
            .collect(Collectors.toMap(Privilege::getName, privilege -> privilege));

        Set<Role> newRoles = EnumSet.allOf(RoleType.class).stream()
            .filter(roleType -> !existingRoles.contains(roleType))
            .map(roleType -> {
                Set<Privilege> privileges = roleType.getPrivileges().stream()
                    .map(privilegeMap::get)
                    .collect(Collectors.toSet());
                return new Role(roleType, privileges);
            })
            .collect(Collectors.toSet());

        if (!newRoles.isEmpty())
            roleRepository.saveAll(newRoles);
    }

}