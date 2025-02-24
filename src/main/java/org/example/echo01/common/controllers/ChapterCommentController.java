package org.example.echo01.common.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.ChapterCommentRequest;
import org.example.echo01.common.dto.response.ChapterCommentResponse;
import org.example.echo01.common.services.IChapterCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class ChapterCommentController {
    private final IChapterCommentService commentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChapterCommentResponse> addComment(
            @Valid @RequestBody ChapterCommentRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(commentService.addComment(request, currentUser));
    }

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<ChapterCommentResponse>> getChapterComments(
            @PathVariable Long chapterId
    ) {
        return ResponseEntity.ok(commentService.getChapterComments(chapterId));
    }

    @GetMapping("/chapter/{chapterId}/root")
    public ResponseEntity<List<ChapterCommentResponse>> getRootComments(
            @PathVariable Long chapterId
    ) {
        return ResponseEntity.ok(commentService.getRootComments(chapterId));
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<ChapterCommentResponse>> getCommentReplies(
            @PathVariable Long commentId
    ) {
        return ResponseEntity.ok(commentService.getCommentReplies(commentId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChapterCommentResponse> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody ChapterCommentRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(commentService.updateComment(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        commentService.deleteComment(id, currentUser);
        return ResponseEntity.noContent().build();
    }
} 