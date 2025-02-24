package org.example.echo01.common.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.ChapterReactionRequest;
import org.example.echo01.common.dto.response.ChapterReactionResponse;
import org.example.echo01.common.services.IChapterReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reactions")
@RequiredArgsConstructor
public class ChapterReactionController {
    private final IChapterReactionService reactionService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChapterReactionResponse> addReaction(
            @Valid @RequestBody ChapterReactionRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(reactionService.addReaction(request, currentUser));
    }

    @DeleteMapping("/chapter/{chapterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeReaction(
            @PathVariable Long chapterId,
            @AuthenticationPrincipal User currentUser
    ) {
        reactionService.removeReaction(chapterId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<Map<String, Integer>> getReactionCounts(
            @PathVariable Long chapterId
    ) {
        return ResponseEntity.ok(reactionService.getReactionCounts(chapterId));
    }

    @GetMapping("/chapter/{chapterId}/users")
    public ResponseEntity<List<ChapterReactionResponse>> getChapterReactions(
            @PathVariable Long chapterId
    ) {
        return ResponseEntity.ok(reactionService.getChapterReactions(chapterId));
    }
} 