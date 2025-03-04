package com.danilo.roombooking.domain.role;

import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.privilege.Privilege;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SequenceGenerator(name = "role_seq", sequenceName = "role_sequence", allocationSize = 1)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @Column(name = "Id", columnDefinition = "BIGINT UNSIGNED")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "Name", columnDefinition = "VARCHAR(50) NOT NULL UNIQUE")
    @Enumerated(EnumType.STRING)
    private RoleType name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "Role_Privilege",
        joinColumns = @JoinColumn(name = "RoleId"),
        inverseJoinColumns = @JoinColumn(name = "PrivilegeId")
    )
    private Set<Privilege> privileges;

    public Role(RoleType name, Set<Privilege> privileges) {
        this.name = name;
        this.privileges = privileges;
    }
}