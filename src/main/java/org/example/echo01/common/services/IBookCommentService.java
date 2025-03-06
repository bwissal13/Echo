package org.example.echo01.common.services;

import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.BookCommentRequest;
import org.example.echo01.common.dto.response.BookCommentResponse;

import java.util.List;

public interface IBookCommentService {
    BookCommentResponse addComment(BookCommentRequest request, User currentUser);
    List<BookCommentResponse> getBookComments(Long bookId);
    List<BookCommentResponse> getRootComments(Long bookId);
    List<BookCommentResponse> getCommentReplies(Long commentId);
} 