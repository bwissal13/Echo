package org.example.echo01.common.services;

import org.example.echo01.common.dto.request.AddToBookmarkRequest;
import org.example.echo01.common.dto.request.CreateBookmarkGroupRequest;
import org.example.echo01.common.dto.response.BookmarkGroupResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class IBookmarkServiceTest {

    @Mock
    private IBookmarkService bookmarkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBookmarkGroup() {
        CreateBookmarkGroupRequest request = new CreateBookmarkGroupRequest();
        when(bookmarkService.createBookmarkGroup(request)).thenReturn(new BookmarkGroupResponse());

        BookmarkGroupResponse response = bookmarkService.createBookmarkGroup(request);
        assertNotNull(response);
        verify(bookmarkService, times(1)).createBookmarkGroup(request);
    }

    @Test
    void testGetBookmarkGroups() {
        Pageable pageable = mock(Pageable.class);
        when(bookmarkService.getBookmarkGroups(pageable)).thenReturn(Page.empty());

        Page<BookmarkGroupResponse> responses = bookmarkService.getBookmarkGroups(pageable);
        assertNotNull(responses);
        verify(bookmarkService, times(1)).getBookmarkGroups(pageable);
    }

    // Add similar tests for addToBookmark, removeFromBookmark, and deleteBookmarkGroup
} 