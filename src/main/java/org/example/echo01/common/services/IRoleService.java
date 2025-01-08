package org.example.echo01.common.services;

import org.example.echo01.common.dto.request.RoleChangeRequest;
import org.example.echo01.common.dto.response.RoleChangeRequestResponse;
import java.util.List;

public interface IRoleService {
    void createRoleChangeRequest(RoleChangeRequest requestDto);
    List<RoleChangeRequestResponse> getPendingRequests();
    void processRequest(Long id, boolean approved, String comment);
    List<RoleChangeRequestResponse> getCurrentUserRequests();
} 