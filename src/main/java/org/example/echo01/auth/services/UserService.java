package org.example.echo01.auth.services;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.dto.request.UpdateProfileRequest;
import org.example.echo01.auth.dto.response.UserResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.enums.Role;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.auth.repositories.TokenRepository;
import org.example.echo01.auth.repositories.OTPRepository;
import org.example.echo01.common.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final OTPRepository otpRepository;

    public UserResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        
        if (request.getFirstname() != null) {
            user.setFirstname(request.getFirstname());
        }
        if (request.getLastname() != null) {
            user.setLastname(request.getLastname());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }

        userRepository.save(user);
        return mapToUserResponse(user);
    }

    public UserResponse getUserById(Long id) {
        try {
            logger.debug("Attempting to find user with ID: {}", id);
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", id);
                        return new CustomException("User not found with ID: " + id);
                    });
            logger.debug("Found user: {}", user.getEmail());
            return mapToUserResponse(user);
        } catch (Exception e) {
            logger.error("Error retrieving user with ID {}: {}", id, e.getMessage(), e);
            throw new CustomException("Error retrieving user: " + e.getMessage());
        }
    }

    public Page<UserResponse> getAllUsers(int page, int size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> users;
        
        if (search != null && !search.isEmpty()) {
            users = userRepository.findByFirstnameContainingOrLastnameContainingOrEmailContaining(
                    search, search, search, pageRequest);
        } else {
            users = userRepository.findAll(pageRequest);
        }
        
        return users.map(this::mapToUserResponse);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .bio(user.getBio())
                .profilePicture(user.getProfilePicture())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .emailVerified(user.isEmailVerified())
                .build();
    }

    @Transactional
    public UserResponse updateUserRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));
        user.setRole(role);
        userRepository.save(user);
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUserStatus(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
        return mapToUserResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));
        
        // First delete all associated records
        otpRepository.deleteAllByUser(user);
        tokenRepository.deleteAllByUser(user);
        
        // Then delete the user
        userRepository.delete(user);
    }
} 