package org.example.echo01.auth.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.dto.request.UpdateProfileRequest;
import org.example.echo01.auth.dto.response.UserResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.enums.Role;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.auth.repositories.TokenRepository;
import org.example.echo01.auth.repositories.OTPRepository;
import org.example.echo01.auth.services.IUserService;
import org.example.echo01.common.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final int MAX_PAGE_SIZE = 50;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final OTPRepository otpRepository;

    @Override
    public UserResponse getCurrentUserProfile() {
        logger.debug("Fetching current user profile");
        User user = getCurrentUser();
        logger.debug("Retrieved profile for user: {}", user.getEmail());
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        logger.debug("Updating profile for current user");
        User user = getCurrentUser();
        
        boolean updated = false;
        
        if (StringUtils.hasText(request.getFirstname())) {
            user.setFirstname(request.getFirstname().trim());
            updated = true;
        }
        if (StringUtils.hasText(request.getLastname())) {
            user.setLastname(request.getLastname().trim());
            updated = true;
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
            updated = true;
        }
        if (StringUtils.hasText(request.getProfilePicture())) {
            user.setProfilePicture(request.getProfilePicture().trim());
            updated = true;
        }

        if (updated) {
            user = userRepository.save(user);
            logger.debug("Profile updated successfully for user: {}", user.getEmail());
        } else {
            logger.debug("No changes detected in profile update request");
        }
        
        return mapToUserResponse(user);
    }

    @Override
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

    @Override
    public Page<UserResponse> getAllUsers(int page, int size, String search) {
        logger.debug("Fetching users - page: {}, size: {}, search: {}", page, size, search);
        
        // Validate pagination parameters
        if (page < 0) {
            throw new CustomException("Page number cannot be negative");
        }
        if (size <= 0 || size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> users;
        
        try {
            if (StringUtils.hasText(search)) {
                users = userRepository.searchUsers(search.trim(), pageRequest);
            } else {
                users = userRepository.findAll(pageRequest);
            }
            
            logger.debug("Found {} users", users.getTotalElements());
            return users.map(this::mapToUserResponse);
        } catch (Exception e) {
            logger.error("Error fetching users: {}", e.getMessage(), e);
            throw new CustomException("Error fetching users: " + e.getMessage());
        }
    }

    @Override
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

    @Override
    @Transactional
    public UserResponse updateUserRole(Long id, Role role) {
        logger.debug("Updating role to {} for user ID: {}", role, id);
        
        if (role == null) {
            throw new CustomException("Role cannot be null");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new CustomException("User not found");
                });
                
        user.setRole(role);
        user = userRepository.save(user);
        logger.debug("Role updated successfully for user: {}", user.getEmail());
        
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long id, boolean enabled) {
        logger.debug("Updating enabled status to {} for user ID: {}", enabled, id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new CustomException("User not found");
                });
                
        user.setEnabled(enabled);
        user = userRepository.save(user);
        logger.debug("Status updated successfully for user: {}", user.getEmail());
        
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        logger.debug("Starting deletion process for user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new CustomException("User not found");
                });
        
        try {
            // First delete all associated records
            logger.debug("Deleting OTP records for user: {}", user.getEmail());
            otpRepository.deleteAllByUser(user);
            
            logger.debug("Deleting token records for user: {}", user.getEmail());
            tokenRepository.deleteAllByUser(user);
            
            // Then delete the user
            logger.debug("Deleting user: {}", user.getEmail());
            userRepository.delete(user);
            
            logger.info("Successfully deleted user with email: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Error during user deletion process for ID {}: {}", id, e.getMessage(), e);
            throw new CustomException("Failed to delete user: " + e.getMessage());
        }
    }
} 