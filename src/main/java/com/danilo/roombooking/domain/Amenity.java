package com.danilo.roombooking.domain;

import com.danilo.roombooking.domain.room.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.Set;

@Entity
@Table
@Data
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SequenceGenerator(name = "amenity_seq", sequenceName = "amenity_sequence", allocationSize = 1)
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "amenity_seq")
    @Column(name = "Id", columnDefinition = "BIGINT UNSIGNED")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "Name", columnDefinition = "VARCHAR(50) NOT NULL UNIQUE")
    private String name;

    @ManyToMany(mappedBy = "amenities")
    private Set<Room> rooms;
}
