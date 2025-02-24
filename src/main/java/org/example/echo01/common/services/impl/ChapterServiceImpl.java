package org.example.echo01.common.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.ChapterRequest;
import org.example.echo01.common.dto.response.ChapterResponse;
import org.example.echo01.common.entities.Book;
import org.example.echo01.common.entities.Chapter;
import org.example.echo01.common.mappers.ChapterMapper;
import org.example.echo01.common.repositories.BookRepository;
import org.example.echo01.common.repositories.ChapterRepository;
import org.example.echo01.common.services.IChapterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements IChapterService {
    private final ChapterRepository chapterRepository;
    private final BookRepository bookRepository;
    private final ChapterMapper chapterMapper;

    @Override
    @Transactional
    public ChapterResponse createChapter(ChapterRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Book book = bookRepository.findByIdAndAuthorId(request.getBookId(), currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found or you don't have permission"));

        if (request.getOrderNumber() == null) {
            Integer maxOrder = chapterRepository.findMaxOrderNumberByBookId(book.getId());
            request.setOrderNumber(maxOrder != null ? maxOrder + 1 : 1);
        }

        Chapter chapter = chapterMapper.toEntity(request, book);
        chapter = chapterRepository.save(chapter);
        return chapterMapper.toResponse(chapter);
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterResponse getChapterById(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));
        return chapterMapper.toResponse(chapter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChapterResponse> getChaptersByBook(Long bookId, Pageable pageable) {
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book not found");
        }
        return chapterRepository.findByBookIdOrderByOrderNumberAsc(bookId, pageable)
                .map(chapterMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChapterResponse> searchInBook(Long bookId, String query, Pageable pageable) {
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book not found");
        }
        return chapterRepository.searchInBook(bookId, query, pageable)
                .map(chapterMapper::toResponse);
    }

    @Override
    @Transactional
    public ChapterResponse updateChapter(Long id, ChapterRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        if (!bookRepository.existsByIdAndAuthorId(chapter.getBook().getId(), currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to update this chapter");
        }

        chapterMapper.partialUpdate(request, chapter);
        chapter = chapterRepository.save(chapter);
        return chapterMapper.toResponse(chapter);
    }

    @Override
    @Transactional
    public void deleteChapter(Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        if (!bookRepository.existsByIdAndAuthorId(chapter.getBook().getId(), currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to delete this chapter");
        }

        chapterRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ChapterResponse restoreChapter(Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        if (!bookRepository.existsByIdAndAuthorId(chapter.getBook().getId(), currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to restore this chapter");
        }

        chapter.setDeleted(false);
        chapter = chapterRepository.save(chapter);
        return chapterMapper.toResponse(chapter);
    }
} 