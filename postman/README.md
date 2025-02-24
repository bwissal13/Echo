# Echo01 API Postman Collection

This directory contains Postman files for testing the Echo01 API.

## Files

- `Echo01.postman_collection.json`: The main collection file containing all API requests
- `Echo01.postman_environment.json`: Environment variables for the collection

## Setup Instructions

1. Install [Postman](https://www.postman.com/downloads/)
2. Import the collection:
   - Open Postman
   - Click "Import" button
   - Select `Echo01.postman_collection.json`

3. Import the environment:
   - Click "Import" button
   - Select `Echo01.postman_environment.json`
   - Select the "Echo01 Environment" from the environment dropdown

## Testing Flow

1. **Authentication**
   - Register a new user
   - Verify email with OTP
   - Resend OTP if needed
   - Login to get your authentication tokens
   - Use refresh token to get new access token when expired

2. **Role Management**
   - Request role change (e.g., to become an author)
   - View your role change requests
   - Admins can view, approve, or reject role requests

3. **User Management**
   - Get your profile
   - Update your profile details
   - Admins can manage other users

4. **Books**
   - Create new books
   - Get public books
   - Search books by genre or keywords
   - Update book details
   - Delete books (soft delete)
   - View your own books
   - Increment book views
   - Make books public/private

5. **Chapters**
   - Create chapters for your books
   - Get chapters by book
   - Update chapter content
   - Delete chapters (soft delete)

6. **Comments**
   - Add comments to chapters
   - Reply to existing comments
   - Get chapter comments
   - Get comment replies
   - Update your comments
   - Delete your comments

7. **Reactions**
   - Add reactions to chapters
   - Remove your reactions
   - Get reaction counts
   - Get users who reacted

8. **Subscriptions**
   - Subscribe to books
   - Subscribe to chapters
   - Get your subscriptions
   - Unsubscribe

9. **Trash Management**
   - View deleted books
   - View deleted chapters
   - Restore items from trash
   - Permanently delete items

## Environment Variables

- `baseUrl`: API base URL (default: http://localhost:8080)
- `token`: JWT authentication token (set automatically after login)
- `refreshToken`: Refresh token for getting new JWT tokens

## Available Endpoints

### Authentication
- POST `/api/v1/auth/register` - Register new user
- POST `/api/v1/auth/verify-email` - Verify email with OTP
- POST `/api/v1/auth/resend-otp` - Resend OTP
- POST `/api/v1/auth/login` - Login user
- POST `/api/v1/auth/refresh` - Refresh access token

### Role Management
- POST `/api/v1/role-requests` - Request role change
- GET `/api/v1/role-requests/me` - Get my role requests
- GET `/api/v1/role-requests` - Get all role requests (Admin)
- PUT `/api/v1/role-requests/{id}/approve` - Approve role request (Admin)
- PUT `/api/v1/role-requests/{id}/reject` - Reject role request (Admin)

### User Management
- GET `/api/v1/users/me` - Get current user profile
- PUT `/api/v1/users/me` - Update current user profile
- GET `/api/v1/users/{id}` - Get user by ID (Admin)
- GET `/api/v1/users` - Get all users (Admin)
- PUT `/api/v1/users/{id}/role` - Update user role (Admin)
- PUT `/api/v1/users/{id}/status` - Update user status (Admin)
- DELETE `/api/v1/users/{id}` - Delete user (Admin)

### Books
- POST `/api/v1/books` - Create new book
- GET `/api/v1/books` - Get public books
- GET `/api/v1/books/me` - Get my books
- GET `/api/v1/books/genre/{genre}` - Get books by genre
- GET `/api/v1/books/search` - Search books
- PUT `/api/v1/books/{id}` - Update book
- DELETE `/api/v1/books/{id}` - Delete book (move to trash)
- POST `/api/v1/books/{id}/views` - Increment book views
- PUT `/api/v1/books/{id}/visibility/public` - Make a book public
- PUT `/api/v1/books/{id}/visibility/private` - Make a book private

### Chapters
- POST `/api/v1/chapters` - Create chapter
- GET `/api/v1/chapters/{id}` - Get chapter by ID
- GET `/api/v1/chapters/book/{bookId}` - Get chapters by book
- PUT `/api/v1/chapters/{id}` - Update chapter
- DELETE `/api/v1/chapters/{id}` - Delete chapter (move to trash)

### Comments
- POST `/api/v1/comments` - Add comment
- GET `/api/v1/comments/chapter/{chapterId}` - Get chapter comments
- GET `/api/v1/comments/chapter/{chapterId}/root` - Get root comments
- GET `/api/v1/comments/{commentId}/replies` - Get comment replies
- PUT `/api/v1/comments/{id}` - Update comment
- DELETE `/api/v1/comments/{id}` - Delete comment

### Reactions
- POST `/api/v1/reactions` - Add reaction
- DELETE `/api/v1/reactions/chapter/{chapterId}` - Remove reaction
- GET `/api/v1/reactions/chapter/{chapterId}` - Get reaction counts
- GET `/api/v1/reactions/chapter/{chapterId}/users` - Get users who reacted

### Subscriptions
- POST `/api/v1/subscriptions/books` - Subscribe to book
- DELETE `/api/v1/subscriptions/books/{bookId}` - Unsubscribe from book
- POST `/api/v1/subscriptions/chapters` - Subscribe to chapter
- DELETE `/api/v1/subscriptions/chapters/{chapterId}` - Unsubscribe from chapter
- GET `/api/v1/subscriptions/books/me` - Get my book subscriptions
- GET `/api/v1/subscriptions/chapters/me` - Get my chapter subscriptions
- POST `/api/v1/subscriptions/users/{userId}` - Subscribe to user
- DELETE `/api/v1/subscriptions/users/{userId}` - Unsubscribe from user
- GET `/api/v1/subscriptions/users/me/subscriptions` - Get users I'm subscribed to
- GET `/api/v1/subscriptions/users/me/subscribers` - Get my subscribers
- GET `/api/v1/subscriptions/users/{userId}/status` - Check subscription status

### Trash
- GET `/api/v1/trash/books` - Get deleted books
- PUT `/api/v1/trash/books/{id}/restore` - Restore book from trash
- DELETE `/api/v1/trash/books/{id}` - Permanently delete book
- GET `/api/v1/trash/chapters` - Get deleted chapters
- GET `/api/v1/trash/chapters/books/{bookId}` - Get deleted chapters by book
- PUT `/api/v1/trash/chapters/{id}/restore` - Restore chapter from trash
- DELETE `/api/v1/trash/chapters/{id}` - Permanently delete chapter

## Response Status Codes

- 200: Success
- 201: Created
- 204: No Content
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error 