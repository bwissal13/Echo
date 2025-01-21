package org.example.echo01.common.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.services.UserService;
import org.example.echo01.common.dto.request.RoleChangeRequest;
import org.example.echo01.common.dto.response.RoleChangeRequestResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.repositories.RoleChangeRequestRepository;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.common.exceptions.CustomException;
import org.example.echo01.common.services.IRoleService;
import org.example.echo01.common.mapper.RoleChangeRequestMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final RoleChangeRequestRepository roleChangeRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RoleChangeRequestMapper roleChangeRequestMapper;

    @Override
    @Transactional
    public void createRoleChangeRequest(RoleChangeRequest requestDto) {
        User currentUser = userService.getCurrentUser();

        if (!roleChangeRequestRepository.findByUserAndProcessedFalse(currentUser).isEmpty()) {
            throw new CustomException("You already have a pending role change request");
        }

        var roleRequest = roleChangeRequestMapper.toEntity(requestDto);
        roleRequest.setUser(currentUser);
        roleChangeRequestRepository.save(roleRequest);
    }

    @Override
    public List<RoleChangeRequestResponse> getPendingRequests() {
        return roleChangeRequestRepository.findAllByProcessedFalse()
                .stream()
                .map(roleChangeRequestMapper::toResponse)
                .toList();
    }

    @Override
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

    @Override
    public List<RoleChangeRequestResponse> getCurrentUserRequests() {
        User currentUser = userService.getCurrentUser();
        return roleChangeRequestRepository.findByUserAndProcessedFalse(currentUser)
                .stream()
                .map(roleChangeRequestMapper::toResponse)
                .toList();
    }
} 