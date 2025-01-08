package org.example.echo01.auth.services;

import lombok.RequiredArgsConstructor;
import org.example.echo01.common.dto.request.RoleChangeRequest;
import org.example.echo01.common.dto.response.RoleChangeRequestResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.repositories.RoleChangeRequestRepository;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.common.exceptions.CustomException;
import org.example.echo01.common.entities.RoleChangeRequest as RoleChangeRequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleChangeRequestRepository roleChangeRequestRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    @Transactional
    public void createRoleChangeRequest(RoleChangeRequest request) {
        User currentUser = authenticationService.getCurrentUser();

        if (!roleChangeRequestRepository.findByUserAndProcessedFalse(currentUser).isEmpty()) {
            throw new CustomException("You already have a pending role change request");
        }

        var roleRequest = RoleChangeRequestEntity.builder()
                .user(currentUser)
                .requestedRole(request.getRequestedRole())
                .reason(request.getReason())
                .approved(false)
                .processed(false)
                .build();

        roleChangeRequestRepository.save(roleRequest);
    }

    public List<RoleChangeRequestResponse> getPendingRequests() {
        return roleChangeRequestRepository.findAllByProcessedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void processRequest(Long id, boolean approved, String comment) {
        var request = roleChangeRequestRepository.findByIdAndProcessedFalse(id)
                .orElseThrow(() -> new CustomException("Role change request not found"));

        request.setProcessed(true);
        request.setApproved(approved);
        request.setAdminComment(comment);

        if (approved) {
            var user = request.getUser();
            user.setRole(request.getRequestedRole());
            userRepository.save(user);
        }

        roleChangeRequestRepository.save(request);
    }

    public List<RoleChangeRequestResponse> getCurrentUserRequests() {
        User currentUser = authenticationService.getCurrentUser();
        return roleChangeRequestRepository.findByUserAndProcessedFalse(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RoleChangeRequestResponse mapToResponse(RoleChangeRequestEntity request) {
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