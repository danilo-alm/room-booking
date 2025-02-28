package com.danilo.roombooking.domain.authority;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AuthorityId implements Serializable {
    private Long user;
    private String authority;
}

