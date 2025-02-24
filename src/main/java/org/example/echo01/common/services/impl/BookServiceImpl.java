package org.example.echo01.common.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.common.dto.request.CreateBookRequest;
import org.example.echo01.common.dto.request.UpdateBookRequest;
import org.example.echo01.common.dto.response.BookResponse;
import org.example.echo01.common.entities.Book;
import org.example.echo01.common.enums.Genre;
import org.example.echo01.common.mappers.BookMapper;
import org.example.echo01.common.repositories.BookRepository;
import org.example.echo01.common.services.IBookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements IBookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Book book = bookMapper.toEntity(request, currentUser);
        book = bookRepository.save(book);
        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        return bookMapper.toResponse(book);
    }

    @Transactional(readOnly = true)
    public BookResponse getDeletedBookById(Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Book book = bookRepository.findDeletedByIdAndAuthorId(id, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found in trash"));
        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findByIsPublicTrue(pageable)
                .map(bookMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getBooksByGenre(Genre genre, Pageable pageable) {
        return bookRepository.findByGenreAndIsPublicTrue(genre, pageable)
                .map(bookMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> searchPublicBooks(String query, Pageable pageable) {
        return bookRepository.searchPublicBooks(query, pageable)
                .map(bookMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getBooksByAuthor(Long authorId, Pageable pageable) {
        if (!userRepository.existsById(authorId)) {
            throw new IllegalArgumentException("Author not found");
        }
        return bookRepository.findByAuthorId(authorId, pageable)
                .map(bookMapper::toResponse);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Book book = bookRepository.findByIdAndAuthorId(id, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found or you don't have permission"));
        
        bookMapper.partialUpdate(request, book);
        book = bookRepository.save(book);
        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!bookRepository.existsByIdAndAuthorId(id, currentUser.getId())) {
            throw new IllegalArgumentException("Book not found or you don't have permission");
        }
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BookResponse incrementViews(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        book.setViews(book.getViews() + 1);
        book = bookRepository.save(book);
        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional
    public BookResponse makeBookPublic(Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!bookRepository.existsByIdAndAuthorId(id, currentUser.getId())) {
            throw new IllegalArgumentException("Book not found or you don't have permission");
        }
        
        bookRepository.makePublic(id, currentUser.getId());
        return getBookById(id);
    }

    @Override
    @Transactional
    public BookResponse makeBookPrivate(Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!bookRepository.existsByIdAndAuthorId(id, currentUser.getId())) {
            throw new IllegalArgumentException("Book not found or you don't have permission");
        }
        
        bookRepository.makePrivate(id, currentUser.getId());
        return getBookById(id);
    }

    @Override
    @Transactional
    public BookResponse restoreBook(Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Book book = bookRepository.findDeletedByIdAndAuthorId(id, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found in trash or you don't have permission"));
        
        bookRepository.restore(id);
        // Refresh the book from database after restore
        book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found after restore"));
        return bookMapper.toResponse(book);
    }
} 