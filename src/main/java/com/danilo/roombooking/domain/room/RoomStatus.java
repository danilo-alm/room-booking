package com.danilo.roombooking.domain.room;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Hashtable;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum RoomStatus {
    AVAILABLE("Available"),
    OCCUPIED("Occupied"),
    MAINTENANCE("In Maintenance");

    private final String value;
    private static final Map<String, String> ROOM_TYPE_MAP;

    static {
        ROOM_TYPE_MAP = new Hashtable<>();
        for (RoomType roomType : RoomType.values()) {
            ROOM_TYPE_MAP.put(roomType.name(), roomType.getValue());
        }
    }

    public static Map<String, String> getMap() {
        return ROOM_TYPE_MAP;
    }
}
