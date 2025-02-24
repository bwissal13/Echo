package org.example.echo01.common.controllers;

import lombok.RequiredArgsConstructor;
import org.example.echo01.common.dtos.UserSubscriptionDto;
import org.example.echo01.common.services.IUserSubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions/users")
@RequiredArgsConstructor
public class UserSubscriptionController {

    private final IUserSubscriptionService userSubscriptionService;

    @PostMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserSubscriptionDto> subscribeToUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userSubscriptionService.subscribeToUser(userId));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unsubscribeFromUser(@PathVariable Long userId) {
        userSubscriptionService.unsubscribeFromUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/subscriptions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserSubscriptionDto>> getMySubscriptions(Pageable pageable) {
        return ResponseEntity.ok(userSubscriptionService.getMySubscriptions(pageable));
    }

    @GetMapping("/me/subscribers")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserSubscriptionDto>> getMySubscribers(Pageable pageable) {
        return ResponseEntity.ok(userSubscriptionService.getMySubscribers(pageable));
    }

    @GetMapping("/{userId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isSubscribedToUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userSubscriptionService.isSubscribedToUser(userId));
    }
} 