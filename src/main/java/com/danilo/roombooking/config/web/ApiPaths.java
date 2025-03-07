package com.danilo.roombooking.config.web;

public final class ApiPaths {
    private ApiPaths() {}

    public static final String BASE_API = "/api";

    public static final class User {
        public static final String ROOT = BASE_API + "/user";
        public static final String CREATE = "";
        public static final String GET = "";
        public static final String DELETE = "/{id}";
    }

    public static final class Room {
        public static final String ROOT = BASE_API + "/room";
        public static final String CREATE = "";
        public static final String GET = "";
        public static final String GET_BY_ID = "/id/{id}";
        public static final String GET_BY_IDENTIFIER = "/identifier/{identifier}";
        public static final String GET_TYPES = "/type";
        public static final String GET_STATUS = "/status";
        public static final String GET_FILTER = "/filter";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
    }

    public static final class Amenity {
        public static final String ROOT = BASE_API + "/amenity";
        public static final String CREATE = "";
        public static final String GET = "";
        public static final String GET_BY_ID = "/id/{id}";
        public static final String DELETE = "/{id}";
    }

    public static final class Booking {
        public static final String ROOT = BASE_API + "/booking";
        public static final String CREATE = "";
        public static final String GET = "";
        public static final String GET_BY_ID = "/id/{id}";
        public static final String GET_BY_USERID = "/user/{userId}";
        public static final String GET_BY_ROOMID = "/room/{roomId}";
        public static final String GET_FILTER = "/filter";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
    }
}
