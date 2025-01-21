# Echo01 API Documentation

This directory contains Postman collection and environment files for testing the Echo01 API.

## Files

- `Echo01.postman_collection.json`: Contains all API endpoints and example requests
- `Echo01.postman_environment.json`: Contains environment variables

## Setup Instructions

1. Install [Postman](https://www.postman.com/downloads/)
2. Import the collection file:
   - Open Postman
   - Click "Import" button
   - Select `Echo01.postman_collection.json`

3. Import the environment file:
   - Click "Import" button again
   - Select `Echo01.postman_environment.json`

4. Select the environment:
   - Click the environment dropdown in the top right
   - Select "Echo01 Local Environment"

## Authentication

Most endpoints require authentication. Here's how to authenticate:

1. Use the "Register" endpoint to create a new user
2. Verify your email using the "Verify OTP" endpoint
3. Use the "Login" endpoint to get an access token
4. The access token will be automatically set in the environment variables
5. All subsequent requests will use this token automatically

## Available Endpoints

### Authentication
- POST `/api/v1/auth/register` - Register new user
- POST `/api/v1/auth/verify-otp` - Verify OTP code
- POST `/api/v1/auth/resend-otp` - Resend OTP code
- POST `/api/v1/auth/login` - Login
- POST `/api/v1/auth/refresh-token` - Refresh access token
- POST `/api/v1/auth/logout` - Logout

### Users
- GET `/api/v1/users/me` - Get current user profile
- PUT `/api/v1/users/me` - Update current user profile
- GET `/api/v1/users/{id}` - Get user by ID (Admin only)
- GET `/api/v1/users` - Get all users (Admin only)
- PUT `/api/v1/users/{id}/role` - Update user role (Admin only)
- PUT `/api/v1/users/{id}/status` - Update user status (Admin only)
- DELETE `/api/v1/users/{id}` - Delete user (Admin only)

## Response Formats

All responses follow this general format:

```json
{
    "accessToken": "jwt_token",  // Only in auth responses
    "user": {
        "id": 1,
        "firstname": "John",
        "lastname": "Doe",
        "email": "john@example.com",
        "bio": "Software Developer",
        "role": "USER",
        "enabled": true,
        "emailVerified": true
    },
    "message": "Success message"  // Optional
}
```

## Error Handling

Errors follow this format:

```json
{
    "message": "Error description",
    "timestamp": "2024-01-21T12:00:00Z",
    "status": 400,
    "path": "/api/v1/endpoint"
}
```

## Rate Limiting

The API implements rate limiting:
- 10 requests per minute for OTP endpoints
- Exceeded limits return 429 Too Many Requests
- Headers include remaining requests and reset time 