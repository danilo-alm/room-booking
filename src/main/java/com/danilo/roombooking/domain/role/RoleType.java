package com.danilo.roombooking.domain.role;

import com.danilo.roombooking.domain.privilege.PrivilegeType;
import lombok.Getter;

import java.util.Set;

@Getter
public enum RoleType {
    ROLE_ADMIN(Set.of(PrivilegeType.VIEW_USERS, PrivilegeType.CREATE_USERS, PrivilegeType.EDIT_USERS, PrivilegeType.DELETE_USERS,
        PrivilegeType.VIEW_AMENITY, PrivilegeType.CREATE_AMENITY, PrivilegeType.EDIT_AMENITY, PrivilegeType.DELETE_AMENITY,
        PrivilegeType.VIEW_ROOMS, PrivilegeType.CREATE_ROOMS, PrivilegeType.EDIT_ROOMS, PrivilegeType.DELETE_ROOMS,
        PrivilegeType.VIEW_BOOKINGS, PrivilegeType.CREATE_BOOKINGS, PrivilegeType.EDIT_BOOKINGS, PrivilegeType.DELETE_BOOKINGS)),

    ROLE_MANAGER(Set.of(PrivilegeType.VIEW_AMENITY, PrivilegeType.CREATE_AMENITY, PrivilegeType.EDIT_AMENITY, PrivilegeType.DELETE_AMENITY,
        PrivilegeType.VIEW_ROOMS, PrivilegeType.CREATE_ROOMS, PrivilegeType.EDIT_ROOMS, PrivilegeType.DELETE_ROOMS,
        PrivilegeType.VIEW_BOOKINGS, PrivilegeType.CREATE_BOOKINGS, PrivilegeType.EDIT_BOOKINGS, PrivilegeType.DELETE_BOOKINGS)),

    ROLE_USER(Set.of(PrivilegeType.VIEW_AMENITY,
        PrivilegeType.VIEW_ROOMS,
        PrivilegeType.VIEW_BOOKINGS, PrivilegeType.EDIT_OWN_BOOKINGS, PrivilegeType.DELETE_OWN_BOOKINGS, PrivilegeType.REQUEST_BOOKING));

    private final Set<PrivilegeType> privileges;

    RoleType(Set<PrivilegeType> privileges) {
        this.privileges = privileges;
    }
}
