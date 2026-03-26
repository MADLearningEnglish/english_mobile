# 📖 Spring Framework Backend API Specification

This document provides the standard requirements for the backend API intended for the "MAD Learning English" mobile app.

---

## 🏗️ 1. Global Architecture

### **1.1 Base URL**
Suggested prefix: `https://api.mad-learning-english.com/api/v1`

### **1.2 Common Response Structure**
All endpoints must return a consistent JSON wrapper:
```json
{
  "message": "Success message or detailed error",
  "statusCode": 200,
  "data": { ... }
}
```

### **1.3 HTTP Status Codes**
- `200 OK`: Successful request.
- `201 Created`: Successful POST (creation).
- `400 Bad Request`: Validation errors or missing fields.
- `401 Unauthorized`: Missing or invalid Bearer token.
- `403 Forbidden`: Insufficient permissions.
- `404 Not Found`: Resource not found.
- `500 Server Error`: Internal issues.

---

## 🔐 2. Authentication & User API

### 🚀 **User Registration**
- **Endpoint**: `POST /auth/register` (or `POST /api/v1/users`)
- **Body**: `CreateUserRequest`
```json
{
  "email": "user@example.com",
  "password": "hashed_password",
  "fullName": "Full Name",
  "avatar": "optional_url"
}
```

### 🔑 **User Login**
- **Endpoint**: `POST /auth/login`
- **Body**: `LoginRequest` (username, password)
- **Response**: `LoginResponse`
```json
{
  "accessToken": "ey...",
  "refreshToken": "ey...",
  "expiresAt": 123456789,
  "user": {
    "email": "user@example.com",
    "fullName": "User Name",
    "avatar": "url",
    "role": 1
  }
}
```

### 🔄 **Refresh Token**
- **Endpoint**: `POST /auth/refresh`
- **Body**: `{ "refreshToken": "..." }`
- **Response**: `{ "accessToken": "...", "refreshToken": "..." }`

### 🛡️ **Current User Status**
- **Endpoint**: `GET /auth/me`
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `UserInfo` (data object)

---

## 📚 3. Book API

### 🧩 **Get Books by Genre**
- **Endpoint**: `GET /books/genres/{genresId}`
- **Query Params**: `page` (int), `size` (int)
- **Response**: `List<BookResponse>`
```json
{
  "id": 1,
  "title": "Learning English",
  "language": "EN",
  "coverUrl": "https://...",
  "genresName": "Education",
  "authors": "John Doe",
  "lastReadNumberPage": 5,
  "progressPercent": 12.5,
  "isFavorite": true
}
```

### 📖 **Recommended Books**
- **Endpoint**: `GET /books/recommend`
- **Response**: `List<BookResponse>`

### 📜 **Reading History**
- **Endpoint**: `GET /books/history`
- **Query Params**: `page` (int), `size` (int)
- **Response**: `List<BookResponse>`

### 💎 **Book Details**
- **Endpoint**: `GET /books/{bookId}`
- **Response**: `BookResponse` (Full object including `chapters`)

### ❤️ **Update Favorite Status**
- **Endpoint**: `PUT /books/{bookId}`
- **Query Params**: `isFavorite` (boolean)
- **Response**: `{ "data": true }`

### 📄 **Get Book Pages**
- **Endpoint**: `GET /books/{bookId}/pages`
- **Query Params**: `pageNumbers` (List of integers e.g. `1,2,3`)
- **Response**: `List<PageResponse>`

---

## 🏷️ 4. Genre API

### 🎨 **List Genres**
- **Endpoint**: `GET /genres`
- **Response**: `List<GenreResponse>`
```json
{
  "id": 1,
  "name": "Fiction"
}
```

---

## 🛠️ 5. Implementation Suggestions for Spring Boot

- **Framework**: Spring Boot 3+ (Java 17/21)
- **Security**: Spring Security + JWT (JSON Web Token)
- **Persistence**: Spring Data JPA (Hibernate)
- **Database**: MySQL / PostgreSQL
- **Documentation**: SpringDoc OpenAPI (Swagger) to automatically generate UI at `/swagger-ui.html`
- **DTO Mapping**: MapStruct for entity-to-dto conversion.
