package com.danilo.roombooking.domain.room_amenity;

import com.danilo.roombooking.domain.Amenity;
import com.danilo.roombooking.domain.room.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Room_Amenity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(RoomAmenityId.class)
public class RoomAmenity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoomId", columnDefinition = "BIGINT UNSIGNED", foreignKey = @ForeignKey(name = "FK_RoomAmenity_Room"))
    private Room room;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "AmenityId", columnDefinition = "BIGINT UNSIGNED", foreignKey = @ForeignKey(name = "FK_RoomAmenity_Amenity"))
    private Amenity amenity;

}
