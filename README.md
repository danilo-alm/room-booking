# Room Booking API 🏨📅

## Project Status 🚧
🚀 This project is currently under active development. Some features may be incomplete or subject to change. Stay tuned! 🚀

## Overview 🏫
Room Booking API is a system that allows users to register rooms (such as university classrooms) and enables authorized users to book them as needed. 🏠✅

## Features ✨
- 🏢 Room registration and management
- 👤 User registration with authentication and authorization 🔑
- 📅 Room booking with filtering options 🔍
- 🛠️ Management of amenities associated with rooms

## Technologies Used 🛠️
- ☕ Java Spring Boot
- 🏛️ Hibernate & JPA
- 🛢️ MariaDB
- 🐳 Docker & Docker Compose
- 📜 Flyway for database migrations

## API Endpoints 🌐

### User 👥
| Method | Endpoint         | Description                |
|--------|-----------------|----------------------------|
| ➕ POST   | `/api/user`     | Create a new user         |
| 📄 GET    | `/api/user`     | Get all users             |
| ❌ DELETE | `/api/user/{id}` | Delete a user by ID       |

### Room 🏢
| Method | Endpoint                      | Description                         |
|--------|--------------------------------|-------------------------------------|
| ➕ POST   | `/api/room`                    | Create a new room                  |
| 📄 GET    | `/api/room`                    | Get all rooms                      |
| 🔍 GET    | `/api/room/id/{id}`            | Get room by ID                     |
| 🔢 GET    | `/api/room/identifier/{identifier}` | Get room by identifier          |
| 🔄 GET    | `/api/room/type`               | Get available room types           |
| 🔄 GET    | `/api/room/status`             | Get available room statuses        |
| 🎯 GET    | `/api/room/filter`             | Filter rooms based on criteria     |
| ✏️ PUT    | `/api/room/{id}`               | Update room information            |
| ❌ DELETE | `/api/room/{id}`               | Delete a room by ID                |

### Amenity 🛠️
| Method | Endpoint        | Description                |
|--------|----------------|----------------------------|
| ➕ POST   | `/api/amenity` | Create a new amenity       |
| 📄 GET    | `/api/amenity` | Get all amenities          |
| ❌ DELETE | `/api/amenity/{id}` | Delete an amenity by ID  |

### Booking 📅
| Method | Endpoint                      | Description                         |
|--------|--------------------------------|-------------------------------------|
| ➕ POST   | `/api/booking`                 | Create a new booking               |
| 📄 GET    | `/api/booking`                 | Get all bookings                   |
| 🔍 GET    | `/api/booking/id/{id}`         | Get booking by ID                  |
| 👤 GET    | `/api/booking/user/{userId}`   | Get bookings by user ID            |
| 🏢 GET    | `/api/booking/room/{roomId}`   | Get bookings by room ID            |
| 🎯 GET    | `/api/booking/filter`          | Filter bookings based on criteria  |
| ✏️ PUT    | `/api/booking/{id}`            | Update booking details             |
| ❌ DELETE | `/api/booking/{id}`            | Delete a booking by ID             |

## Request and Response Formats 📩📤

### User 👤
#### Request 📥
```json
{
  "username": "johndoe",
  "password": "securepassword",
  "fullName": "John Doe",
  "enabled": true,
  "email": "john.doe@example.com",
  "authorities": ["ROLE_USER"]
}
```

#### Response 📤
```json
{
  "id": 1,
  "username": "johndoe",
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "createdAt": "2025-03-02T12:00:00Z",
  "updatedAt": "2025-03-02T12:00:00Z",
  "lastLogin": null,
  "failedLoginAttempts": 0,
  "enabled": true,
  "authorities": ["ROLE_USER"]
}
```

### Room 🏢
#### Request 📥
```json
{
  "identifier": "A101",
  "name": "Lecture Hall A101",
  "description": "Spacious lecture hall",
  "capacity": 100,
  "status": "AVAILABLE",
  "type": "LECTURE_HALL",
  "amenitiesIds": [1, 2]
}
```

#### Response 📤
```json
{
  "id": 1,
  "identifier": "A101",
  "name": "Lecture Hall A101",
  "description": "Spacious lecture hall",
  "capacity": 100,
  "status": "AVAILABLE",
  "type": "LECTURE_HALL",
  "amenities": ["Projector", "WiFi"],
  "createdAt": "2025-03-02T12:00:00Z",
  "updatedAt": "2025-03-02T12:00:00Z"
}
```

### Amenity 🛠️
#### Request 📥
```json
{
   "name": "Projector"
}
```

#### Response 📥
```json
{
   "id": 1,
   "name": "Projector"
}
```

### Booking 📅 (WIP 🚧)
#### Request
```json
{
  "roomId": 1,
  "userId": 2,
  "startTime": "2025-03-02T14:00:00Z",
  "endTime": "2025-03-02T16:00:00Z"
}
```
🚧 UserId should not be passed in manually. WILL CHANGE! 🚧

#### Response
```json
{
  "id": 1,
  "roomId": 1,
  "userId": 2,
  "startTime": "2025-03-02T14:00:00Z",
  "endTime": "2025-03-02T16:00:00Z",
  "createdAt": "2025-03-02T12:00:00Z",
  "updatedAt": "2025-03-02T12:00:00Z"
}
```

## TODO 📝
- 🔐 JWT authentication
- 🔒 Secure endpoints
- 👑 Create a default admin user
- 🧪 Integration tests
- 🐳 Full dockerization
- 📜 Link to Postman Collection
- 📝 Update documentation / README

## License 📜
This project is licensed under the GNU General Public License v3.0 ⚖️
