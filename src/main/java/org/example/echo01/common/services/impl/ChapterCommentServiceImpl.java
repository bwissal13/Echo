package org.example.echo01.common.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.ChapterCommentRequest;
import org.example.echo01.common.dto.response.ChapterCommentResponse;
import org.example.echo01.common.entities.Chapter;
import org.example.echo01.common.entities.ChapterComment;
import org.example.echo01.common.mappers.ChapterCommentMapper;
import org.example.echo01.common.repositories.ChapterCommentRepository;
import org.example.echo01.common.repositories.ChapterRepository;
import org.example.echo01.common.services.IChapterCommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterCommentServiceImpl implements IChapterCommentService {
    private final ChapterCommentRepository commentRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterCommentMapper commentMapper;

    @Override
    @Transactional
    public ChapterCommentResponse addComment(ChapterCommentRequest request, User currentUser) {
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        ChapterComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            
            if (!parentComment.getChapter().getId().equals(chapter.getId())) {
                throw new IllegalArgumentException("Parent comment does not belong to the same chapter");
            }
        }

        ChapterComment comment = commentMapper.toEntity(request, chapter, currentUser, parentComment);
        comment = commentRepository.save(comment);
        return commentMapper.toResponse(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterCommentResponse> getChapterComments(Long chapterId) {
        if (!chapterRepository.existsById(chapterId)) {
            throw new IllegalArgumentException("Chapter not found");
        }
        return commentRepository.findByChapterId(chapterId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterCommentResponse> getRootComments(Long chapterId) {
        if (!chapterRepository.existsById(chapterId)) {
            throw new IllegalArgumentException("Chapter not found");
        }
        return commentRepository.findByChapterIdAndParentCommentIsNull(chapterId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterCommentResponse> getCommentReplies(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("Comment not found");
        }
        return commentRepository.findByParentCommentId(commentId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChapterCommentResponse updateComment(Long id, ChapterCommentRequest request, User currentUser) {
        ChapterComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to update this comment");
        }

        commentMapper.partialUpdate(request, comment);
        comment = commentRepository.save(comment);
        return commentMapper.toResponse(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id, User currentUser) {
        ChapterComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to delete this comment");
        }

        commentRepository.deleteById(id);
    }
} 