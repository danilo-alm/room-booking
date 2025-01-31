package com.danilo.roombooking.domain.authority;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AuthorityId implements Serializable {
    private BigInteger user;
    private String authority;
}

