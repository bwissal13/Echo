package org.example.echo01.common.controllers;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.services.UserService;
import org.example.echo01.common.dto.request.BookSubscriptionRequest;
import org.example.echo01.common.dto.response.SubscriptionResponse;
import org.example.echo01.common.entities.Book;
import org.example.echo01.common.entities.BookSubscription;
import org.example.echo01.common.mappers.BookSubscriptionMapper;
import org.example.echo01.common.repositories.BookRepository;
import org.example.echo01.common.repositories.BookSubscriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions/books")
@RequiredArgsConstructor
public class BookSubscriptionController {
    private final BookSubscriptionRepository subscriptionRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final BookSubscriptionMapper subscriptionMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionResponse> subscribe(
            @RequestBody BookSubscriptionRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        if (subscriptionRepository.existsByBookIdAndUserId(book.getId(), currentUser.getId())) {
            throw new IllegalStateException("Already subscribed to this book");
        }

        BookSubscription subscription = subscriptionMapper.toEntity(request, book, currentUser);
        subscription = subscriptionRepository.save(subscription);

        return ResponseEntity.ok(subscriptionMapper.toResponse(subscription));
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unsubscribe(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User currentUser
    ) {
        if (!subscriptionRepository.existsByBookIdAndUserId(bookId, currentUser.getId())) {
            throw new IllegalStateException("Not subscribed to this book");
        }

        subscriptionRepository.deleteByBookIdAndUserId(bookId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<SubscriptionResponse>> getMySubscriptions(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable
    ) {
        Page<BookSubscription> subscriptions = subscriptionRepository
                .findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable);

        return ResponseEntity.ok(subscriptions.map(subscriptionMapper::toResponse));
    }

    @GetMapping("/books/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SubscriptionResponse>> getBookSubscribers(
            @PathVariable Long bookId,
            Pageable pageable
    ) {
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book not found");
        }

        Page<BookSubscription> subscriptions = subscriptionRepository
                .findByBookIdOrderByCreatedAtDesc(bookId, pageable);

        return ResponseEntity.ok(subscriptions.map(subscriptionMapper::toResponse));
    }
} 