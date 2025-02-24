package com.danilo.roombooking.specification;

import com.danilo.roombooking.domain.room.Room;
import com.danilo.roombooking.domain.room.RoomStatus;
import com.danilo.roombooking.domain.room.RoomType;
import com.danilo.roombooking.domain.room_amenity.RoomAmenity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigInteger;
import java.util.Set;

public class RoomSpecification {

    public static Specification<Room> hasCapacityGreaterThanOrEqualTo(Integer capacity) {
        return (root, query, builder) ->
            capacity == null ? null : builder.greaterThanOrEqualTo(root.get("capacity"), capacity);
    }

    public static Specification<Room> hasCapacityLessThanOrEqualTo(Integer capacity) {
        return (root, query, builder) ->
            capacity == null ? null : builder.lessThanOrEqualTo(root.get("capacity"), capacity);
    }

    public static Specification<Room> nameContains(final String name) {
        return (root, query, builder) ->
            name == null ? null : builder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Room> hasStatus(RoomStatus status) {
        return (root, query, builder) ->
            status == null ? null : builder.equal(root.get("status"), status);
    }

    public static Specification<Room> hasType(RoomType type) {
        return (root, query, builder) ->
            type == null ? null : builder.equal(root.get("type"), type);
    }

    public static Specification<Room> hasAmenities(Set<BigInteger> amenityIds) {
        return (root, query, builder) -> {
            if (amenityIds == null || amenityIds.isEmpty()) {
                return null;
            }
            Join<Room, RoomAmenity> roomAmenities = root.join("amenities");

            if (query != null) {
                query.groupBy(root.get("id"));
                query.having(builder.equal(builder.countDistinct(roomAmenities
                    .get("amenity").get("id")), amenityIds.size()));
            }

            return roomAmenities.get("amenity").get("id").in(amenityIds);
        };
    }
}
