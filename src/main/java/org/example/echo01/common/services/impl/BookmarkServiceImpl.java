package org.example.echo01.common.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.AddToBookmarkRequest;
import org.example.echo01.common.dto.request.CreateBookmarkGroupRequest;
import org.example.echo01.common.dto.response.BookmarkGroupResponse;
import org.example.echo01.common.entities.Book;
import org.example.echo01.common.entities.BookmarkGroup;
import org.example.echo01.common.mappers.BookmarkGroupMapper;
import org.example.echo01.common.repositories.BookRepository;
import org.example.echo01.common.repositories.BookmarkGroupRepository;
import org.example.echo01.common.services.IBookmarkService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements IBookmarkService {
    private final BookmarkGroupRepository bookmarkGroupRepository;
    private final BookRepository bookRepository;
    private final BookmarkGroupMapper bookmarkGroupMapper;

    @Override
    @Transactional
    public BookmarkGroupResponse createBookmarkGroup(CreateBookmarkGroupRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BookmarkGroup group = bookmarkGroupMapper.toEntity(request, currentUser);
        group = bookmarkGroupRepository.save(group);
        return bookmarkGroupMapper.toResponse(group);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookmarkGroupResponse> getBookmarkGroups(Pageable pageable) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return bookmarkGroupRepository.findByUserId(currentUser.getId(), pageable)
                .map(bookmarkGroupMapper::toResponse);
    }

    @Override
    @Transactional
    public BookmarkGroupResponse addToBookmark(AddToBookmarkRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        BookmarkGroup group = bookmarkGroupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Bookmark group not found"));
                
        if (!group.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to modify this bookmark group");
        }
        
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
                
        group.getBooks().add(book);
        group = bookmarkGroupRepository.save(group);
        return bookmarkGroupMapper.toResponse(group);
    }

    @Override
    @Transactional
    public void removeFromBookmark(Long groupId, Long bookId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        BookmarkGroup group = bookmarkGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Bookmark group not found"));
                
        if (!group.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to modify this bookmark group");
        }
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
                
        group.getBooks().remove(book);
        bookmarkGroupRepository.save(group);
    }

    @Override
    @Transactional
    public void deleteBookmarkGroup(Long groupId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (!bookmarkGroupRepository.existsByIdAndUserId(groupId, currentUser.getId())) {
            throw new IllegalArgumentException("Bookmark group not found or you don't have permission");
        }
        
        bookmarkGroupRepository.deleteById(groupId);
    }
} 