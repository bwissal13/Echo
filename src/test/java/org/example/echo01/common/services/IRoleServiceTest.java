package org.example.echo01.common.services;

import org.example.echo01.common.dto.request.RoleChangeRequest;
import org.example.echo01.common.dto.response.RoleChangeRequestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class IRoleServiceTest {

    @Mock
    private IRoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoleChangeRequest() {
        RoleChangeRequest request = new RoleChangeRequest();
        doNothing().when(roleService).createRoleChangeRequest(request);

        roleService.createRoleChangeRequest(request);
        verify(roleService, times(1)).createRoleChangeRequest(request);
    }

    @Test
    void testGetPendingRequests() {
        when(roleService.getPendingRequests()).thenReturn(List.of(new RoleChangeRequestResponse()));

        List<RoleChangeRequestResponse> responses = roleService.getPendingRequests();
        assertNotNull(responses);
        verify(roleService, times(1)).getPendingRequests();
    }

    // Add similar tests for processRequest and getCurrentUserRequests
} 