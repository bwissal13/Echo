package org.example.echo01.auth.services;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.dto.request.RoleChangeRequest;
import org.example.echo01.auth.dto.response.RoleChangeRequestResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.repositories.RoleChangeRequestRepository;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.common.exceptions.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleChangeRequestRepository roleChangeRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public void createRoleChangeRequest(RoleChangeRequest request) {
        User currentUser = userService.getCurrentUser();
        
        // Check if user already has pending requests
        if (!roleChangeRequestRepository.findByUserAndProcessedFalse(currentUser).isEmpty()) {
            throw new CustomException("You already have a pending role change request");
        }

        var roleRequest = org.example.echo01.auth.entities.RoleChangeRequest.builder()
                .user(currentUser)
                .requestedRole(request.getRequestedRole())
                .reason(request.getReason())
                .build();

        roleChangeRequestRepository.save(roleRequest);
    }

    public List<RoleChangeRequestResponse> getPendingRequests() {
        return roleChangeRequestRepository.findAllByProcessedFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processRequest(Long id, boolean approved, String comment) {
        var request = roleChangeRequestRepository.findByIdAndProcessedFalse(id)
                .orElseThrow(() -> new CustomException("Role change request not found"));

        request.setProcessed(true);
        request.setApproved(approved);
        request.setAdminComment(comment);

        if (approved) {
            request.getUser().setRole(request.getRequestedRole());
            userRepository.save(request.getUser());
        }

        roleChangeRequestRepository.save(request);
    }

    public List<RoleChangeRequestResponse> getCurrentUserRequests() {
        User currentUser = userService.getCurrentUser();
        return roleChangeRequestRepository.findByUserAndProcessedFalse(currentUser)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RoleChangeRequestResponse mapToResponse(org.example.echo01.auth.entities.RoleChangeRequest request) {
        return RoleChangeRequestResponse.builder()
                .id(request.getId())
                .userId(request.getUser().getId())
                .userEmail(request.getUser().getEmail())
                .requestedRole(request.getRequestedRole())
                .reason(request.getReason())
                .approved(request.isApproved())
                .processed(request.isProcessed())
                .adminComment(request.getAdminComment())
                .createdAt(request.getCreatedAt())
                .build();
    }
} 