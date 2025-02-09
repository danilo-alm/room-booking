package com.danilo.roombooking.domain.room;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Hashtable;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum RoomType {
    STANDARD_CLASSROOM("Standard Classroom"),
    LECTURE_HALL("Lecture Hall"),
    SEMINAR_ROOM("Seminar Room"),
    AUDITORIUM("Auditorium"),
    COMPUTER_LAB("Computer Lab"),
    SCIENCE_LAB("Science Lab"),
    ENGINEERING_LAB("Engineering Lab"),
    MEDICAL_LAB("Medical Lab"),
    STUDY_ROOM("Study Room"),
    GROUP_DISCUSSION_ROOM("Group Discussion Room"),
    LIBRARY_READING_ROOM("Library Reading Room"),
    MUSIC_ROOM("Music Room"),
    ART_STUDIO("Art Studio"),
    DRAMA_THEATER_ROOM("Drama/Theater Room"),
    RECORDING_STUDIO("Recording Studio"),
    CONFERENCE_ROOM("Conference Room"),
    FACULTY_OFFICE("Faculty Office"),
    EXAMINATION_ROOM("Examination Room");

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
