package com.danilo.roombooking.domain.privilege;

import com.danilo.roombooking.domain.role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "privilege_seq", sequenceName = "privilege_sequence", allocationSize = 1)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "privilege_seq")
    @Column(name = "Id", columnDefinition = "BIGINT UNSIGNED")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "Name", columnDefinition = "VARCHAR(50) NOT NULL UNIQUE")
    @Enumerated(EnumType.STRING)
    @EqualsAndHashCode.Include
    private PrivilegeType name;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "privileges")
    private Set<Role> roles;

    public Privilege(PrivilegeType privilegeType) {
        this.name = privilegeType;
    }
}
