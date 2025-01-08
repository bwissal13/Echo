package org.example.echo01.auth.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.echo01.common.dto.request.RoleChangeRequest;
import org.example.echo01.common.dto.response.RoleChangeRequestResponse;
import org.example.echo01.common.services.IRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    @PostMapping("/request")
    public ResponseEntity<?> requestRoleChange(@Valid @RequestBody RoleChangeRequest request) {
        roleService.createRoleChangeRequest(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Role change request submitted successfully");
        response.put("success", "true");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleChangeRequestResponse>> getPendingRequests() {
        return ResponseEntity.ok(roleService.getPendingRequests());
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String comment = request.get("adminComment");
        roleService.processRequest(id, true, comment);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Role change request approved successfully");
        response.put("success", "true");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String comment = request.get("adminComment");
        roleService.processRequest(id, false, comment);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Role change request rejected successfully");
        response.put("success", "true");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<RoleChangeRequestResponse>> getMyRequests() {
        return ResponseEntity.ok(roleService.getCurrentUserRequests());
    }
} 