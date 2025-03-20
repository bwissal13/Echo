package org.example.echo01.common.services;

import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.BookCommentRequest;
import org.example.echo01.common.dto.response.BookCommentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class IBookCommentServiceTest {

    @Mock
    private IBookCommentService bookCommentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddComment() {
        BookCommentRequest request = new BookCommentRequest();
        User user = new User();
        when(bookCommentService.addComment(request, user)).thenReturn(new BookCommentResponse());

        BookCommentResponse response = bookCommentService.addComment(request, user);
        assertNotNull(response);
        verify(bookCommentService, times(1)).addComment(request, user);
    }

    @Test
    void testGetBookComments() {
        when(bookCommentService.getBookComments(1L)).thenReturn(List.of(new BookCommentResponse()));

        List<BookCommentResponse> responses = bookCommentService.getBookComments(1L);
        assertNotNull(responses);
        verify(bookCommentService, times(1)).getBookComments(1L);
    }

    // Add similar tests for getRootComments and getCommentReplies
} 