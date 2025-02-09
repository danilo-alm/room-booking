package com.danilo.roombooking.domain.authority;

import com.danilo.roombooking.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "Authorities")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@IdClass(AuthorityId.class)
public class Authority implements GrantedAuthority {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", columnDefinition = "BIGINT UNSIGNED", foreignKey = @ForeignKey(name = "FK_Authorities_Users"))
    private User user;

    @Id
    @Column(name = "Authority", columnDefinition = "VARCHAR(50) NOT NULL")
    private String authority;
}
