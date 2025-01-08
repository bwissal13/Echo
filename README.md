# Echo01 - Online Reading Platform

## Overview
Echo01 is a sophisticated online reading platform that enables users to publish, read, and interact with books. The platform supports multiple user roles (Reader, Author, Admin) and implements secure authentication and authorization mechanisms.

## Project Structure

```
src/main/java/org/example/echo01/
├── auth/                    # Authentication related components
│   ├── config/             # Security configurations
│   ├── controllers/        # Authentication endpoints
│   ├── dto/                # Data transfer objects
│   ├── entities/           # Authentication related entities
│   ├── repositories/       # Data access layer
│   └── services/          # Business logic
├── common/                 # Shared components
│   ├── dto/               # Common DTOs
│   ├── entities/          # Domain entities
│   ├── repositories/      # Data repositories
│   └── services/         # Business services
└── config/                # Application configurations
```

## Features

### Authentication & Authorization
- Secure JWT-based authentication
- Role-based access control
- Token refresh mechanism
- Email verification
- Password reset functionality

### User Management
- User registration and profile management
- Role change requests
- Admin approval workflow

### Content Management
- Book creation and management
- Chapter organization
- Comment system
- Notification system

## API Endpoints

### Authentication Endpoints
```
POST /api/v1/auth/register
- Register a new user
- Body: { firstName, lastName, email, password }
- Returns: AuthenticationResponse with JWT token

POST /api/v1/auth/login
- Authenticate user
- Body: { email, password }
- Returns: AuthenticationResponse with JWT token

POST /api/v1/auth/refresh-token
- Refresh JWT token
- Header: Authorization: Bearer <refresh-token>
- Returns: New access token

POST /api/v1/auth/verify-email
- Verify user's email
- Query param: token
- Returns: Success message

POST /api/v1/auth/forgot-password
- Initiate password reset
- Body: { email }
- Returns: Success message

POST /api/v1/auth/reset-password
- Reset password
- Body: { token, newPassword }
- Returns: Success message
```

### Role Management Endpoints
```
POST /api/v1/roles/request
- Request role change
- Body: { requestedRole, reason }
- Returns: RoleChangeRequestResponse

GET /api/v1/roles/requests/current
- Get current user's role requests
- Returns: List of RoleChangeRequestResponse

GET /api/v1/roles/requests/pending
- Get all pending role requests (Admin only)
- Returns: List of RoleChangeRequestResponse

PUT /api/v1/roles/requests/{requestId}/process
- Process role change request (Admin only)
- Body: { approved, adminComment }
- Returns: RoleChangeRequestResponse
```

### Book Management Endpoints
```
POST /api/v1/books
- Create new book (Author only)
- Body: { title, description, genre }
- Returns: BookResponse

GET /api/v1/books
- Get all books
- Query params: page, size, sort
- Returns: Page<BookResponse>

GET /api/v1/books/{bookId}
- Get book details
- Returns: BookResponse

PUT /api/v1/books/{bookId}
- Update book (Author only)
- Body: { title, description, genre }
- Returns: BookResponse

DELETE /api/v1/books/{bookId}
- Delete book (Author only)
- Returns: Success message
```

### Chapter Management Endpoints
```
POST /api/v1/books/{bookId}/chapters
- Add chapter to book (Author only)
- Body: { title, content }
- Returns: ChapterResponse

GET /api/v1/books/{bookId}/chapters
- Get all chapters of a book
- Returns: List<ChapterResponse>

PUT /api/v1/books/{bookId}/chapters/{chapterId}
- Update chapter (Author only)
- Body: { title, content }
- Returns: ChapterResponse

DELETE /api/v1/books/{bookId}/chapters/{chapterId}
- Delete chapter (Author only)
- Returns: Success message
```

### Comment System Endpoints
```
POST /api/v1/books/{bookId}/chapters/{chapterId}/comments
- Add comment
- Body: { content }
- Returns: CommentResponse

GET /api/v1/books/{bookId}/chapters/{chapterId}/comments
- Get chapter comments
- Returns: List<CommentResponse>

PUT /api/v1/comments/{commentId}
- Update comment (Owner only)
- Body: { content }
- Returns: CommentResponse

DELETE /api/v1/comments/{commentId}
- Delete comment (Owner or Admin)
- Returns: Success message
```

### Notification Endpoints
```
GET /api/v1/notifications
- Get user notifications
- Returns: List<NotificationResponse>

PUT /api/v1/notifications/{notificationId}/read
- Mark notification as read
- Returns: NotificationResponse
```

## Security

The application implements several security measures:
- JWT-based authentication
- Password encryption using BCrypt
- Role-based access control
- Request validation
- XSS protection
- CSRF protection
- Rate limiting

## Database Schema

The application uses PostgreSQL with Liquibase for database migrations. Key tables include:
- users
- books
- chapters
- comments
- notifications
- role_change_requests
- refresh_tokens

## Getting Started

1. Prerequisites:
   - Java 17
   - PostgreSQL
   - Maven

2. Configuration:
   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. Run the application:
   ```
   ./mvnw spring-boot:run
   ```

4. Access the API at `http://localhost:8080`

## Testing

The application includes:
- Unit tests
- Integration tests
- API tests

Run tests with:
```
./mvnw test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License. 