package org.example.echo01.common.controllers;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.response.ChapterResponse;
import org.example.echo01.common.entities.Chapter;
import org.example.echo01.common.mappers.ChapterMapper;
import org.example.echo01.common.repositories.BookRepository;
import org.example.echo01.common.repositories.ChapterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trash/chapters")
@RequiredArgsConstructor
public class ChapterTrashController {
    private final ChapterRepository chapterRepository;
    private final BookRepository bookRepository;
    private final ChapterMapper chapterMapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ChapterResponse>> getDeletedChapters(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable
    ) {
        Page<Chapter> chapters = chapterRepository.findDeletedByAuthorId(currentUser.getId(), pageable);
        return ResponseEntity.ok(chapters.map(chapterMapper::toResponse));
    }

    @GetMapping("/books/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ChapterResponse>> getDeletedChaptersByBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User currentUser,
            Pageable pageable
    ) {
        if (!bookRepository.existsByIdAndAuthorId(bookId, currentUser.getId())) {
            throw new IllegalArgumentException("Book not found or you don't have permission");
        }

        Page<Chapter> chapters = chapterRepository.findDeletedByBookId(bookId, pageable);
        return ResponseEntity.ok(chapters.map(chapterMapper::toResponse));
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<Void> restoreChapter(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        if (!bookRepository.existsByIdAndAuthorId(chapter.getBook().getId(), currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to restore this chapter");
        }

        chapterRepository.restore(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<Void> permanentlyDeleteChapter(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        if (!bookRepository.existsByIdAndAuthorId(chapter.getBook().getId(), currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to delete this chapter");
        }

        chapterRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 