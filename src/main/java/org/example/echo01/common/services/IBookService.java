package org.example.echo01.common.services;

import org.example.echo01.common.dto.request.CreateBookRequest;
import org.example.echo01.common.dto.request.UpdateBookRequest;
import org.example.echo01.common.dto.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBookService {
    BookResponse createBook(CreateBookRequest request);
    BookResponse getBookById(Long id);
    Page<BookResponse> getAllBooks(Pageable pageable);
    Page<BookResponse> getBooksByAuthor(Long authorId, Pageable pageable);
    BookResponse updateBook(Long id, UpdateBookRequest request);
    void deleteBook(Long id);
    BookResponse incrementViews(Long id);
} 