package com.danilo.roombooking.domain;

import com.danilo.roombooking.domain.room.Room;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Timestamp;

@Entity
@Table(
    uniqueConstraints = {@UniqueConstraint(name = "UQ_Booking", columnNames = {"RoomId", "StartTime", "EndTime"})},
    indexes = {
        @Index(name = "IX_Booking", columnList = "RoomId, StartTime, EndTime"),
        @Index(name = "IX_Booking_UserId", columnList = "UserId"),
        @Index(name = "IX_Booking_Approved", columnList = "Approved")
    }
)
@Data
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SequenceGenerator(name = "amenity_seq", sequenceName = "amenity_sequence", allocationSize = 1)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "amenity_seq")
    @Column(name = "Id", columnDefinition = "BIGINT UNSIGNED")
    @EqualsAndHashCode.Include
    private Long id;

    @JoinColumn(name = "RoomId", columnDefinition = "BIGINT UNSIGNED")
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @Column(name = "Approved", columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
    private Boolean approved;

    @JoinColumn(name = "RequestedBy", columnDefinition = "BIGINT UNSIGNED NOT NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    private User requestedBy;

    @JoinColumn(name = "ApprovedBy", columnDefinition = "BIGINT UNSIGNED")
    @ManyToOne(fetch = FetchType.LAZY)
    private User approvedBy;

    @Column(name = "StartTime", columnDefinition = "TIMESTAMP NOT NULL")
    private Timestamp startTime;

    @Column(name = "EndTime", columnDefinition = "TIMESTAMP NOT NULL")
    private Timestamp endTime;

    @Column(name = "CreatedAt", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "UpdatedAt", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;
}
