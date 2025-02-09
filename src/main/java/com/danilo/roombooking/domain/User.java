package com.danilo.roombooking.domain;

import com.danilo.roombooking.domain.authority.Authority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "Users", indexes = {
    @Index(name = "UX_Users_Email", columnList = "Email"),
    @Index(name = "UX_Users_Username", columnList = "Username"),
})
@SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
@DynamicInsert
@Data
@EqualsAndHashCode(exclude = "authorities")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Column(name = "Id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Authority> authorities;
}
