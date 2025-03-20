package org.example.echo01.common.services.impl;

import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.ChapterRequest;
import org.example.echo01.common.dto.response.ChapterResponse;
import org.example.echo01.common.entities.Book;
import org.example.echo01.common.entities.Chapter;
import org.example.echo01.common.mappers.ChapterMapper;
import org.example.echo01.common.repositories.BookRepository;
import org.example.echo01.common.repositories.ChapterRepository;
import org.example.echo01.common.services.IChapterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChapterServiceImplTest {

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ChapterMapper chapterMapper;

    @InjectMocks
    private ChapterServiceImpl chapterService;

    private User author;
    private Book book;
    private Chapter chapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        author = User.builder()
                .id(1L)
                .firstname("Author")
                .lastname("User")
                .email("author@example.com")
                .build();

        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author(author)
                .build();

        chapter = Chapter.builder()
                .id(1L)
                .title("Test Chapter")
                .book(book)
                .build();

        // Mock SecurityContext and Authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        // Set up the mocked SecurityContext and Authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(author);

        // Set the SecurityContext in the SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createChapter_ShouldReturnChapterResponse() {
        ChapterRequest request = new ChapterRequest("Test Chapter", "Content", 1L, 1);
        when(bookRepository.findByIdAndAuthorId(1L, 1L)).thenReturn(Optional.of(book));
        when(chapterMapper.toEntity(any(ChapterRequest.class), any(Book.class))).thenReturn(chapter);
        when(chapterRepository.save(any(Chapter.class))).thenReturn(chapter);
        when(chapterMapper.toResponse(any(Chapter.class))).thenReturn(new ChapterResponse(
                1L, "Test Chapter", "Content", 1L, 1, Collections.emptyList(), new HashMap<>(), 0, LocalDateTime.now(), LocalDateTime.now()
        ));

        ChapterResponse response = chapterService.createChapter(request);

        assertNotNull(response);
        assertEquals("Test Chapter", response.getTitle());
        verify(chapterRepository, times(1)).save(any(Chapter.class));
    }

    @Test
    void getChapterById_ShouldReturnChapterResponse() {
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(chapterMapper.toResponse(any(Chapter.class))).thenReturn(new ChapterResponse(
                1L, "Test Chapter", "Content", 1L, 1, Collections.emptyList(), new HashMap<>(), 0, LocalDateTime.now(), LocalDateTime.now()
        ));

        ChapterResponse response = chapterService.getChapterById(1L);

        assertNotNull(response);
        assertEquals("Test Chapter", response.getTitle());
        verify(chapterRepository, times(1)).findById(1L);
    }
} 