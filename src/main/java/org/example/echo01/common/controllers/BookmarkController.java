package org.example.echo01.common.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.echo01.common.dto.request.AddToBookmarkRequest;
import org.example.echo01.common.dto.request.CreateBookmarkGroupRequest;
import org.example.echo01.common.dto.response.BookmarkGroupResponse;
import org.example.echo01.common.services.IBookmarkService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final IBookmarkService bookmarkService;

    @PostMapping("/groups")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookmarkGroupResponse> createBookmarkGroup(
            @Valid @RequestBody CreateBookmarkGroupRequest request
    ) {
        return ResponseEntity.ok(bookmarkService.createBookmarkGroup(request));
    }

    @GetMapping("/groups")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BookmarkGroupResponse>> getBookmarkGroups(Pageable pageable) {
        return ResponseEntity.ok(bookmarkService.getBookmarkGroups(pageable));
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookmarkGroupResponse> addToBookmark(
            @Valid @RequestBody AddToBookmarkRequest request
    ) {
        return ResponseEntity.ok(bookmarkService.addToBookmark(request));
    }

    @DeleteMapping("/groups/{groupId}/books/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFromBookmark(
            @PathVariable Long groupId,
            @PathVariable Long bookId
    ) {
        bookmarkService.removeFromBookmark(groupId, bookId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/groups/{groupId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteBookmarkGroup(@PathVariable Long groupId) {
        bookmarkService.deleteBookmarkGroup(groupId);
        return ResponseEntity.noContent().build();
    }
} 