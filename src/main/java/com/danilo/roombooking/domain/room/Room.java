package com.danilo.roombooking.domain.room;

import com.danilo.roombooking.domain.room_amenity.RoomAmenity;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(indexes = {
    @Index(name = "IX_Room_Identifier", columnList = "Identifier")
})
@SequenceGenerator(name = "room_seq", sequenceName = "room_sequence", allocationSize = 1)
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@Builder
@EqualsAndHashCode(exclude = "amenities")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_seq")
    @Column(name = "Id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Column(name = "Identifier", columnDefinition = "VARCHAR(50) NOT NULL UNIQUE")
    private String identifier;

    @Column(name = "Name", columnDefinition = "VARCHAR(50) NOT NULL")
    private String name;

    @Column(name = "Description", columnDefinition = "VARCHAR(100)")
    private String description;

    @Column(name = "Capacity", columnDefinition = "INT NOT NULL")
    private Integer capacity;

    @Column(name = "Status", columnDefinition = "VARCHAR(50) NOT NULL")
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @Column(name = "Type", columnDefinition = "VARCHAR(50) NOT NULL")
    @Enumerated(EnumType.STRING)
    private RoomType type;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoomAmenity> amenities;

    @CreationTimestamp
    @Column(name = "CreatedAt", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;
}
