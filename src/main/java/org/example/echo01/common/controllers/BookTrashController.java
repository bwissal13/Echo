package org.example.echo01.common.controllers;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.response.BookResponse;
import org.example.echo01.common.entities.Book;
import org.example.echo01.common.mappers.BookMapper;
import org.example.echo01.common.repositories.BookRepository;
import org.example.echo01.common.services.IBookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trash/books")
@RequiredArgsConstructor
public class BookTrashController {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final IBookService bookService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BookResponse>> getDeletedBooks(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable
    ) {
        Page<Book> books = bookRepository.findDeletedByAuthorId(currentUser.getId(), pageable);
        return ResponseEntity.ok(books.map(bookMapper::toResponse));
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<BookResponse> restoreBook(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(bookService.restoreBook(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<Void> permanentlyDeleteBook(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (!bookRepository.existsByIdAndAuthorId(id, currentUser.getId())) {
            throw new IllegalArgumentException("Book not found or you don't have permission");
        }

        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 