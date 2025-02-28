package com.danilo.roombooking.specification;

import com.danilo.roombooking.domain.Booking;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;

public class BookingSpecification {

    public static Specification<Booking> hasRoomId(Long roomId) {
        return ((root, query, builder) ->
            roomId == null ? null : builder.equal(root.get("roomId"), roomId));
    }

    public static Specification<Booking> hasUserId(Long userId) {
        return ((root, query, builder) ->
            userId == null ? null : builder.equal(root.get("userId"), userId));
    }

    public static Specification<Booking> hasStartTimeGreaterThanOrEqualTo(Timestamp minStartTime) {
        return ((root, query, builder) ->
            minStartTime == null ? null : builder.greaterThanOrEqualTo(root.get("startTime"), minStartTime));
    }

    public static Specification<Booking> hasEndTimeLessThanOrEqualTo(Timestamp maxEndTime) {
        return ((root, query, builder) ->
            maxEndTime == null ? null : builder.lessThanOrEqualTo(root.get("endTime"), maxEndTime));
    }
}
