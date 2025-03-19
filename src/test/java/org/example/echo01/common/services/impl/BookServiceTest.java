import org.example.echo01.common.dto.request.CreateBookRequest;
import org.example.echo01.common.dto.response.BookResponse;
import org.example.echo01.common.entities.Book;
import org.example.echo01.common.enums.Genre;
import org.example.echo01.common.repositories.BookRepository;
import org.example.echo01.common.services.impl.BookServiceImpl;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.echo01.common.mappers.BookMapper;

import java.util.Collections;
import java.util.Optional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private User author;
    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        author = User.builder()
                .id(1L)
                .firstname("Author")
                .lastname("User")
                .email("author@example.com")
                .role(Role.AUTHOR)
                .enabled(true)
                .build();

        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author(author)
                .build();

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(author);
        SecurityContextHolder.setContext(securityContext);

        when(bookMapper.toEntity(any(CreateBookRequest.class), any(User.class))).thenReturn(book);
        when(bookMapper.toResponse(any(Book.class))).thenReturn(new BookResponse(
                1L, "Test Book", "Description", Genre.ROMANCE, author.getId(), author.getFirstname() + " " + author.getLastname(),
                true, 0, Collections.emptyList(), 0, true, true, true, LocalDateTime.now(), LocalDateTime.now()
        ));
    }

    @Test
    void createBook_ShouldReturnBookResponse() {
        CreateBookRequest request = new CreateBookRequest("Test Book", "Description", Genre.ROMANCE, true);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponse response = bookService.createBook(request);

        assertNotNull(response);
        assertEquals("Test Book", response.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void getBookById_ShouldReturnBookResponse() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponse response = bookService.getBookById(1L);

        assertNotNull(response);
        assertEquals("Test Book", response.getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }
} 