package org.example.echo01.auth.services;

import org.example.echo01.auth.dto.request.UpdateProfileRequest;
import org.example.echo01.auth.dto.response.UserResponse;
import org.example.echo01.auth.dto.response.AuthorWithBooksResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public interface IUserService {
    UserResponse getCurrentUserProfile();
    UserResponse updateProfile(UpdateProfileRequest request);
    UserResponse getUserById(Long id);
    Page<UserResponse> getAllUsers(int page, int size, String search);
    User getCurrentUser();
    UserResponse updateUserRole(Long id, Role role);
    UserResponse updateUserStatus(Long id, boolean enabled);
    void deleteUser(Long id);
    Page<AuthorWithBooksResponse> getAuthors(int page, int size, String search, Sort sort);
} 