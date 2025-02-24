package org.example.echo01.common.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.CreateBookRequest;
import org.example.echo01.common.dto.request.UpdateBookRequest;
import org.example.echo01.common.dto.response.BookResponse;
import org.example.echo01.common.enums.Genre;
import org.example.echo01.common.services.IBookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final IBookService bookService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody CreateBookRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(bookService.createBook(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<Page<BookResponse>> getBooksByGenre(
            @PathVariable Genre genre,
            Pageable pageable
    ) {
        return ResponseEntity.ok(bookService.getBooksByGenre(genre, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(bookService.searchPublicBooks(search, pageable));
        }
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BookResponse>> getMyBooks(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable
    ) {
        return ResponseEntity.ok(bookService.getBooksByAuthor(currentUser.getId(), pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/views")
    public ResponseEntity<BookResponse> incrementViews(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.incrementViews(id));
    }

    @PutMapping("/{id}/visibility/public")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookResponse> makeBookPublic(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(bookService.makeBookPublic(id));
    }

    @PutMapping("/{id}/visibility/private")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookResponse> makeBookPrivate(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(bookService.makeBookPrivate(id));
    }
} 