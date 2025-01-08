package org.example.echo01.common.services;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.services.UserService;
import org.example.echo01.common.dto.request.RoleChangeRequestDto;
import org.example.echo01.common.dto.response.RoleChangeRequestResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.entities.RoleChangeRequest;
import org.example.echo01.common.repositories.RoleChangeRequestRepository;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.common.exceptions.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleChangeRequestRepository roleChangeRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public void createRoleChangeRequest(RoleChangeRequestDto requestDto) {
        User currentUser = userService.getCurrentUser();

        if (!roleChangeRequestRepository.findByUserAndProcessedFalse(currentUser).isEmpty()) {
            throw new CustomException("You already have a pending role change request");
        }

        var roleRequest = RoleChangeRequest.builder()
                .user(currentUser)
                .requestedRole(requestDto.getRequestedRole())
                .reason(requestDto.getReason())
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
        User currentUser = userService.getCurrentUser();
        return roleChangeRequestRepository.findByUserAndProcessedFalse(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RoleChangeRequestResponse mapToResponse(RoleChangeRequest request) {
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