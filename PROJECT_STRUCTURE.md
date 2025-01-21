# Echo01 Project Structure

## Overview
Echo01 is a Spring Boot application that follows a clean, modular architecture. The project is organized into distinct packages based on functionality and domain concerns.

## Package Structure

### 1. Authentication Module (`org.example.echo01.auth`)
This module handles all authentication and user management functionality.

#### 1.1 Controllers
- `AuthenticationController`: Handles authentication endpoints (login, register, refresh token)
- `UserController`: Manages user-related operations (profile, role management)

#### 1.2 DTOs
- **Request DTOs**
  - `LoginRequest`: Login credentials
  - `RegisterRequest`: User registration data
  - `UpdateProfileRequest`: Profile update information
- **Response DTOs**
  - `AuthenticationResponse`: Authentication result with tokens
  - `UserResponse`: User profile data
  - `TokenResponse`: Token information
  - `OTPResponse`: OTP verification details
  - `RefreshTokenResponse`: Refresh token information

#### 1.3 Entities
- `User`: Core user entity implementing Spring Security's UserDetails
- `Token`: JWT token storage entity
- `OTP`: One-Time Password entity for email verification

#### 1.4 Services
- **Interfaces**
  - `IUserService`: User management operations
  - `ITokenService`: Token management
  - `IOTPService`: OTP generation and verification
  - `IRefreshTokenService`: Refresh token handling
- **Implementations**
  - `UserServiceImpl`: User service implementation
  - `TokenServiceImpl`: Token service implementation
  - `OTPServiceImpl`: OTP service implementation
  - `RefreshTokenServiceImpl`: Refresh token service implementation

#### 1.5 Repositories
- `UserRepository`: User data access
- `TokenRepository`: Token storage and retrieval
- `OTPRepository`: OTP management
- `RefreshTokenRepository`: Refresh token storage

### 2. Common Module (`org.example.echo01.common`)
Contains shared components and utilities.

#### 2.1 Audit
- `Auditable`: Base class for entity auditing (created/modified timestamps)

#### 2.2 Exceptions
- `CustomException`: Application-specific exception handling

#### 2.3 DTOs
- **Book DTOs**
  - `CreateBookRequest`
  - `UpdateBookRequest`
  - `BookResponse`
- **Chapter DTOs**
  - `CreateChapterRequest`
  - `UpdateChapterRequest`
  - `ChapterResponse`
- **Notification DTOs**
  - `CreateNotificationRequest`
  - `NotificationResponse`

#### 2.4 Entities
- `BaseEntity`: Common entity properties
- `Book`: Book entity
- `Chapter`: Chapter entity
- `Notification`: Notification entity
- `RoleChangeRequest`: Role change tracking

#### 2.5 Services
- **Interfaces**
  - `IBookService`
  - `INotificationService`
- **Implementations**
  - `BookServiceImpl`
  - `NotificationServiceImpl`
  - `RoleServiceImpl`

#### 2.6 Repositories
- `BookRepository`
- `ChapterRepository`
- `NotificationRepository`

### 3. Configuration (`org.example.echo01.config`)
Application configuration classes.

- `JpaConfig`: JPA and auditing configuration
- `SecurityConfig`: Security settings and JWT configuration
- `RateLimitInterceptor`: API rate limiting
- `WebMvcConfig`: MVC configuration

## Key Features

### 1. Security
- JWT-based authentication
- Role-based access control
- Rate limiting
- Password encryption

### 2. User Management
- User registration with email verification
- OTP verification system
- Profile management
- Role management

### 3. Content Management
- Book creation and management
- Chapter organization
- Notification system

### 4. Technical Features
- Entity auditing
- Exception handling
- DTO mapping
- Repository pagination
- API documentation (Postman collection)

## Testing Structure

### 1. Unit Tests
- Controller tests
- Service tests
- Repository tests

### 2. Integration Tests
- Authentication flow tests
- User management tests
- Content management tests

## Documentation
- Postman collection for API testing
- Environment configuration
- API documentation
- Project structure documentation

## Build and Dependencies
The project uses Maven for dependency management and build automation. Key dependencies include:

- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Lombok
- MapStruct
- JWT
- JavaMail
- Thymeleaf

## Getting Started
Refer to the main README.md for setup instructions and the Postman collection for API testing. 