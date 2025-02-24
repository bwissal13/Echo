package org.example.echo01.common.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.ChapterRequest;
import org.example.echo01.common.dto.response.ChapterResponse;
import org.example.echo01.common.services.IChapterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chapters")
@RequiredArgsConstructor
public class ChapterController {
    private final IChapterService chapterService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChapterResponse> createChapter(
            @Valid @RequestBody ChapterRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(chapterService.createChapter(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChapterResponse> getChapterById(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getChapterById(id));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<Page<ChapterResponse>> getChaptersByBook(
            @PathVariable Long bookId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(chapterService.getChaptersByBook(bookId, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ChapterResponse>> searchChapters(
            @RequestParam Long bookId,
            @RequestParam String query,
            Pageable pageable
    ) {
        return ResponseEntity.ok(chapterService.searchInBook(bookId, query, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChapterResponse> updateChapter(
            @PathVariable Long id,
            @Valid @RequestBody ChapterRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(chapterService.updateChapter(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteChapter(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChapterResponse> restoreChapter(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(chapterService.restoreChapter(id));
    }
} 