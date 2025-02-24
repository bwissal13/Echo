package org.example.echo01.common.controllers;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.services.UserService;
import org.example.echo01.common.dto.request.ChapterSubscriptionRequest;
import org.example.echo01.common.dto.response.SubscriptionResponse;
import org.example.echo01.common.entities.Chapter;
import org.example.echo01.common.entities.ChapterSubscription;
import org.example.echo01.common.mappers.ChapterSubscriptionMapper;
import org.example.echo01.common.repositories.ChapterRepository;
import org.example.echo01.common.repositories.ChapterSubscriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions/chapters")
@RequiredArgsConstructor
public class ChapterSubscriptionController {
    private final ChapterSubscriptionRepository subscriptionRepository;
    private final ChapterRepository chapterRepository;
    private final UserService userService;
    private final ChapterSubscriptionMapper subscriptionMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionResponse> subscribe(
            @RequestBody ChapterSubscriptionRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        if (subscriptionRepository.existsByChapterIdAndUserId(chapter.getId(), currentUser.getId())) {
            throw new IllegalStateException("Already subscribed to this chapter");
        }

        ChapterSubscription subscription = subscriptionMapper.toEntity(request, chapter, currentUser);
        subscription = subscriptionRepository.save(subscription);

        return ResponseEntity.ok(subscriptionMapper.toResponse(subscription));
    }

    @DeleteMapping("/{chapterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unsubscribe(
            @PathVariable Long chapterId,
            @AuthenticationPrincipal User currentUser
    ) {
        if (!subscriptionRepository.existsByChapterIdAndUserId(chapterId, currentUser.getId())) {
            throw new IllegalStateException("Not subscribed to this chapter");
        }

        subscriptionRepository.deleteByChapterIdAndUserId(chapterId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<SubscriptionResponse>> getMySubscriptions(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable
    ) {
        Page<ChapterSubscription> subscriptions = subscriptionRepository
                .findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable);

        return ResponseEntity.ok(subscriptions.map(subscriptionMapper::toResponse));
    }

    @GetMapping("/chapters/{chapterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SubscriptionResponse>> getChapterSubscribers(
            @PathVariable Long chapterId,
            Pageable pageable
    ) {
        if (!chapterRepository.existsById(chapterId)) {
            throw new IllegalArgumentException("Chapter not found");
        }

        Page<ChapterSubscription> subscriptions = subscriptionRepository
                .findByChapterIdOrderByCreatedAtDesc(chapterId, pageable);

        return ResponseEntity.ok(subscriptions.map(subscriptionMapper::toResponse));
    }
} 