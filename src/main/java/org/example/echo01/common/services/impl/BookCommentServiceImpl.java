package org.example.echo01.common.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.BookCommentRequest;
import org.example.echo01.common.dto.response.BookCommentResponse;
import org.example.echo01.common.entities.Book;
import org.example.echo01.common.entities.BookComment;
import org.example.echo01.common.mappers.BookCommentMapper;
import org.example.echo01.common.repositories.BookCommentRepository;
import org.example.echo01.common.repositories.BookRepository;
import org.example.echo01.common.services.IBookCommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookCommentServiceImpl implements IBookCommentService {
    private final BookCommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final BookCommentMapper commentMapper;

    @Override
    @Transactional
    public BookCommentResponse addComment(BookCommentRequest request, User currentUser) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        BookComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            
            if (!parentComment.getBook().getId().equals(book.getId())) {
                throw new IllegalArgumentException("Parent comment does not belong to the same book");
            }
        }

        BookComment comment = BookComment.builder()
                .content(request.getContent())
                .book(book)
                .user(currentUser)
                .parentComment(parentComment)
                .build();

        comment = commentRepository.save(comment);
        return commentMapper.toResponse(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCommentResponse> getBookComments(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book not found");
        }
        return commentRepository.findByBookId(bookId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCommentResponse> getRootComments(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book not found");
        }
        return commentRepository.findByBookIdAndParentCommentIsNull(bookId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCommentResponse> getCommentReplies(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("Comment not found");
        }
        return commentRepository.findByParentCommentId(commentId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }
} 