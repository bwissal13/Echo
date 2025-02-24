package org.example.echo01.common.services;

import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.ChapterCommentRequest;
import org.example.echo01.common.dto.response.ChapterCommentResponse;

import java.util.List;

public interface IChapterCommentService {
    ChapterCommentResponse addComment(ChapterCommentRequest request, User currentUser);
    List<ChapterCommentResponse> getChapterComments(Long chapterId);
    List<ChapterCommentResponse> getRootComments(Long chapterId);
    List<ChapterCommentResponse> getCommentReplies(Long commentId);
    ChapterCommentResponse updateComment(Long id, ChapterCommentRequest request, User currentUser);
    void deleteComment(Long id, User currentUser);
} 