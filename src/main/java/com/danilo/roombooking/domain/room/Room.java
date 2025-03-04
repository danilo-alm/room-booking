package com.danilo.roombooking.domain.room;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.domain.Booking;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_seq")
    @Column(name = "Id", columnDefinition = "BIGINT UNSIGNED")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "Identifier", columnDefinition = "VARCHAR(50) NOT NULL UNIQUE")
    private String identifier;

    @Column(name = "Name", columnDefinition = "VARCHAR(50) NOT NULL")
    private String name;

    @Column(name = "Description", columnDefinition = "VARCHAR(100)")
    private String description;

    @Column(name = "Capacity", columnDefinition = "INT NOT NULL CHECK (Capacity > 0)")
    private Integer capacity;

    @Column(name = "Status", columnDefinition = "VARCHAR(50) NOT NULL")
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @Column(name = "Type", columnDefinition = "VARCHAR(50) NOT NULL")
    @Enumerated(EnumType.STRING)
    private RoomType type;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Room_Amenity",
        joinColumns = @JoinColumn(name = "RoomId"),
        inverseJoinColumns = @JoinColumn(name = "AmenityId")
    )
    private Set<Amenity> amenities;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Booking> bookings;

    @CreationTimestamp
    @Column(name = "CreatedAt", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;
}
