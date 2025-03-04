package com.danilo.roombooking.domain;

import com.danilo.roombooking.domain.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "Users", indexes = {
    @Index(name = "UX_Users_Email", columnList = "Email"),
    @Index(name = "UX_Users_Username", columnList = "Username"),
})
@DynamicInsert
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Column(name = "Id", columnDefinition = "BIGINT UNSIGNED")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "Username", columnDefinition = "VARCHAR(36) NOT NULL UNIQUE")
    private String username;

    @Column(name = "Password",columnDefinition = "CHAR(60) NOT NULL")
    private String password;

    @Column(name = "Enabled", columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE")
    private Boolean enabled;

    @Column(name = "Email", columnDefinition = "VARCHAR(254) NOT NULL UNIQUE")
    private String email;

    @Column(name = "FullName", columnDefinition = "VARCHAR(100) NOT NULL")
    private String fullName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Booking> bookings;

    @CreationTimestamp
    @Column(name = "CreatedAt", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;

    @Column(name = "LastLogin", columnDefinition = "TIMESTAMP")
    private Timestamp lastLogin;

    @Column(name = "FailedLoginAttempts", columnDefinition = "INT NOT NULL DEFAULT 0")
    private Integer failedLoginAttempts;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "User_Role",
        joinColumns = @JoinColumn(name = "UserId"),
        inverseJoinColumns = @JoinColumn(name = "RoleId")
    )
    private Set<Role> roles;
}
